package ru.ibs.tomcatrestart;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import ru.ibs.tomcatrestart.bean.CommitBean;

/**
 *
 * @author NAnishhenko
 */
public class TomcatRestart {

    private static final String CONFIG_FILE_NAME = "properties.txt";
    private static final String CONFIG_REMOTE_FILE_NAME = "properties_remote.txt";
    private String s = File.separator;
    protected boolean targetSystemIsLinux = false;

    private static final int TIME_TO_WAIT = 10000;

    final String host;
    final String user;
    final String password;
    final String zipArchPath;
    final String targetUnpackDir;
    final String tomcatModulesDir;
    final String modules;
    final String pmpClientPath;
    final String sshPmpClientPath;
    final String[] deleteDirPath;
    final String[] deleteFilesPath;
    final String[] deleteDirsPath;
    final String gitPath;
    final Properties properties;

    boolean forceCopy = false;

    public TomcatRestart(String configFileName) throws Exception {
        properties = new Properties();
        properties.load(new FileInputStream(new File(configFileName)));
        host = properties.getProperty("host");
        user = properties.getProperty("user");
        password = properties.getProperty("password");
        zipArchPath = properties.getProperty("zipArchPath");
        targetUnpackDir = properties.getProperty("targetUnpackDir");
        tomcatModulesDir = properties.getProperty("tomcatModulesDir");
        modules = properties.getProperty("modules");
        pmpClientPath = properties.getProperty("pmpClientPath");
        sshPmpClientPath = properties.getProperty("sshPmpClientPath");
        deleteDirPath = splitString(properties.getProperty("deleteDirPath"));
        deleteFilesPath = splitString(properties.getProperty("deleteFilesPath"));
        deleteDirsPath = splitString(properties.getProperty("deleteDirsPath"));
        gitPath = properties.getProperty("gitPath");
    }

    public static void main(String[] args) throws Exception {
//        String output = Scp.createSshClient("192.168.192.111", "hello", "world", 9084, new String[]{"ls -l"});
//        Scp.scpTo("192.168.192.111", 9084, "hello", "world", "/tmp/zxxx.txt", "D:\\tmp\\zxxx.txt");
        List<String> argsList = new ArrayList<>(args.length);
        Collections.addAll(argsList, args);

        TomcatRestart tomcatRestart;
        if (argsList.contains("-rl")) {
            tomcatRestart = new TomcatRestartRemote(getConfigFileName(argsList, CONFIG_REMOTE_FILE_NAME));
            tomcatRestart.setS("/");
            tomcatRestart.setTargetSystemIsLinux(true);
            ((TomcatRestartRemote) tomcatRestart).getFileModificationDate();
        } else if (argsList.contains("-r")) {
            tomcatRestart = new TomcatRestartRemote(getConfigFileName(argsList, CONFIG_REMOTE_FILE_NAME));
        } else {
            tomcatRestart = new TomcatRestart(getConfigFileName(argsList, CONFIG_FILE_NAME));
        }

        if (argsList.contains("-i")) {
            tomcatRestart.setForceCopy(true);
        }
        List<String> particularModules = null;
        if (argsList.contains("-modules")) {
            particularModules = getParticularModules(argsList);
        }

        String host = tomcatRestart.getHost();
        String user = tomcatRestart.getUser();
        String password = tomcatRestart.getPassword();
        String zipArchPath = tomcatRestart.getZipArchPath();
        String targetUnpackDir = tomcatRestart.getTargetUnpackDir();
        String tomcatModulesDir = tomcatRestart.getTomcatModulesDir();
        String modules = tomcatRestart.getModules();
        String pmpClientPath = tomcatRestart.getPmpClientPath();
        String sshPmpClientPath = tomcatRestart.getSshPmpClientPath();
        String[] deleteDirPath = tomcatRestart.getDeleteDirPath();
        String[] deleteFilesPath = tomcatRestart.getDeleteFilesPath();
        String[] deleteDirsPath = tomcatRestart.getDeleteDirsPath();

        if (!argsList.contains("-clean")) {
            tomcatRestart.getPmpClientFromServer(tomcatModulesDir, pmpClientPath, sshPmpClientPath, host, user, password);
            if (particularModules == null) {
                tomcatRestart.copyModulesFromArch(tomcatModulesDir, targetUnpackDir, modules, zipArchPath);
            } else {
                particularModules.forEach(module -> {
                    try {
                        tomcatRestart.copyOneFile(tomcatModulesDir, new File(module), true);
                    } catch (Exception e) {
                        throw new RuntimeException("Copying one file " + module + " in modules mode caused Fatal Error!", e);
                    }
                });
            }
            tomcatRestart.initActions();
            for (String path : deleteDirPath) {
                tomcatRestart.deleteDir(path);
            }
            for (String path : deleteFilesPath) {
                tomcatRestart.deleteFiles(path);
            }
            for (String path : deleteDirsPath) {
                tomcatRestart.deleteDirs(path);
            }
            tomcatRestart.postActions();
        }
        System.out.println("TomcatRestart finished!");
        Thread.sleep(TIME_TO_WAIT);
        System.exit(0);
    }

    private static List<String> getParticularModules(List<String> argsList) {
        int indexOf = argsList.indexOf("-modules");
        List<String> particularModules = new ArrayList<>();
        for (int i = indexOf + 1; i < argsList.size(); i++) {
            if (argsList.get(i).startsWith("-")) {
                break;
            }
            if (new File(argsList.get(i)).exists()) {
                particularModules.add(argsList.get(i));
            }
        }
        if (particularModules.isEmpty()) {
            throw new RuntimeException("There is no particular modules! This is Fatal Error!");
        }
        return particularModules;
    }

    private static String getConfigFileName(List<String> argsList, String defaultConfig) {
        if (argsList.contains("-conf")) {
            return argsList.get(argsList.indexOf("-conf") + 1);
        } else {
            return defaultConfig;
        }
    }

    protected String[] splitString(String str) {
        if (str != null) {
            if (str.contains(",")) {
                return str.split(",");
            } else {
                return new String[]{str};
            }
        } else {
            return new String[0];
        }
    }

    protected void initActions() {
    }

    protected void postActions() {
    }

    protected void setFileModificationDate(File file) {
    }

    protected Date getFileDate(File file) {
        return null;
    }

    protected void copyModulesFromArch(String tomcatModulesDir, String targetUnpackDir, String modules, String zipArchPath) throws Exception {
        String targetModulesDir = targetUnpackDir + modules;
        File targetDir = new File(targetUnpackDir);
        if (!targetDir.exists()) {
            UnZip.unZipIt(zipArchPath, targetDir.getAbsolutePath(), this::checkArchiveFiles);
            copyFiles(targetModulesDir, tomcatModulesDir, false);
        } else if (targetDir.exists() && isForceCopy()) {
            copyFiles(targetModulesDir, tomcatModulesDir, false);
        } else {
            System.out.println("Modules already had been updated!");
        }
    }

    private Map<String, CommitBean> parseJSONcommits(byte[] string) throws Exception {
//        File file = new File("D:\\GIT\\pmp\\pmp\\build\\target\\pmp-dist-all\\modules\\module-pmp\\WEB-INF\\classes\\changelog.json");
//        byte[] bytes = new String(Files.readAllBytes(file.toPath()), "cp1251").getBytes("utf-8");
        byte[] bytes = new String(string, "cp1251").getBytes("utf-8");
//        if (file.exists()) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<CommitBean> readValue = objectMapper.readValue(bytes, new TypeReference<List<CommitBean>>() {
            });
            if (readValue != null) {
                return readValue.stream().collect(Collectors.toMap(CommitBean::getId, obj -> obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }
        return null;
    }

    static byte[] jsonBytes;

    private void getGitLogs(byte[] stringBytes) throws Exception {
        if (jsonBytes == null) {
            Map<String, CommitBean> parseJSONcommits = parseJSONcommits(stringBytes);
//    Git git = Git.open(new File(repositoryDir.getAbsolutePath() + "/.git"));
            CommitBean earliest = parseJSONcommits.values().stream().min((obj1, obj2) -> obj1.getDateAsDate().compareTo(obj2.getDateAsDate())).get();
            CommitBean latest = parseJSONcommits.values().stream().max((obj1, obj2) -> obj1.getDateAsDate().compareTo(obj2.getDateAsDate())).get();
//        parseJSONcommits.values().stream().collect(Collectors.toMap(obj->obj.getCommitterName(), obj->obj.getAuthorName()));
            System.out.println(earliest.getId() + " " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(earliest.getDateAsDate()));
            File repositoryDir = new File(gitPath);
            Git git = Git.open(new File(repositoryDir.getAbsolutePath() + "/.git"));
//        Iterable<RevCommit> iterable = git.log().call();
            Iterable<RevCommit> iterable = git.log().add(git.getRepository().resolve("heads/develop")).call();
            Iterator<RevCommit> iterator = iterable.iterator();
            List<CommitBean> commitBeanList = new ArrayList<>();
            while (iterator.hasNext()) {
                CommitBean commitBean = new CommitBean();
                RevCommit commit = iterator.next();
                PersonIdent authorIdent = commit.getAuthorIdent();
                Date authorDate = authorIdent.getWhen();
                TimeZone authorTimeZone = authorIdent.getTimeZone();
                if (authorDate.before(earliest.getDateAsDate())) {
                    break;
                }
                String id = commit.getId().toString();
                Date date = new Date(commit.getCommitTime() * 1000);
                String name = commit.getCommitterIdent().getName();
                String emailAddress = commit.getCommitterIdent().getEmailAddress();
                String fullMessage = commit.getFullMessage();
//            System.out.println(id + " " + " authorDate: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(authorDate) + " name: " + name + " emailAddress: " + emailAddress + " fullMessage: " + fullMessage);
                commitBean.setId(id);
                commitBean.setAuthorName(name);
                commitBean.setAuthorEmail(emailAddress);
                commitBean.setCommitterEmail(emailAddress);
                commitBean.setCommitterName(name);
                commitBean.setDate(authorDate);
                commitBean.setMessage(fullMessage);
                commitBeanList.add(commitBean);
            }
            Map<String, CommitBean> commitBeanMap = commitBeanList.stream().collect(Collectors.toMap(CommitBean::getId, obj -> obj));
            parseJSONcommits.entrySet().removeIf(entry -> commitBeanMap.containsKey(entry.getKey()));
            List<CommitBean> list = commitBeanMap.values().stream().filter(obj -> !obj.getMessage().startsWith("Merge branch")).filter(obj -> latest.getDateAsDate().before(obj.getDateAsDate())).collect(Collectors.toList());
            Collection<CommitBean> values = parseJSONcommits.values();
            list.addAll(values);
            List<CommitBean> resultList = list.stream().sorted().collect(Collectors.toList());
//        List<CommitBean> resultList = parseJSONcommits.values().stream().sorted().collect(Collectors.toList());
            ObjectMapper objectMapper = new ObjectMapper();
//            File outputfile = new File("D:\\GIT\\pmp\\pmp\\build\\target\\pmp-dist-all\\modules\\module-pmp\\WEB-INF\\classes\\changelogOut.json");
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            objectMapper.writeValue(byteOutputStream, resultList);
            String string = new String(byteOutputStream.toByteArray());

            String replaceAll = string.replaceAll(",", ", ").replace("{", "{ ").replace("},", " }\r\n  ,").replaceFirst("\\[", "[\r\n    ");
            replaceAll = replaceAll.substring(0, replaceAll.lastIndexOf("}]")) + " }\r\n]";
            replaceAll = replaceAll.replace("}]", " } ]");
            jsonBytes = replaceAll.getBytes();
//            if (outputfile.exists()) {
//                outputfile.delete();
//            }
//            Files.write(outputfile.toPath(), jsonBytes, StandardOpenOption.CREATE_NEW);
            System.out.println();
        }
    }

    private void changeFileChangeDate(File file, Date date) {
        if (date != null) {
            file.setLastModified(date.getTime());
        }
    }

    protected void checkArchiveFiles(File file) {
        try {
            checkArchiveFiles(file, getFileDate(file));
        } catch (Exception e) {
            throw new RuntimeException("checkArchiveFiles Exception!", e);
        }
    }

    protected void checkArchiveFiles(File file, Date archiveDate) throws Exception {
        File file2 = new File(file.getParentFile().getAbsolutePath() + File.separator + file.getName().substring(0, file.getName().indexOf(".")) + "2" + file.getName().substring(file.getName().indexOf(".")));
        if (file.getName().endsWith(".war")) {
            ZipFile zipFile = new ZipFile(file);
            final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file2));
            for (Enumeration e = zipFile.entries(); e.hasMoreElements();) {
                ZipEntry entryIn = (ZipEntry) e.nextElement();
                InputStream is = zipFile.getInputStream(entryIn);
                int available = is.available();
                byte[] buf = new byte[available];
                int len;
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                while ((len = is.read(buf)) > 0) {
                    byteArrayOutputStream.write(buf, 0, len);
                }
                byte[] toByteArray = byteArrayOutputStream.toByteArray();
                if (!entryIn.getName().endsWith(".class") && !entryIn.getName().endsWith("/") && !entryIn.getName().endsWith(".wsdl") && !entryIn.getName().endsWith(".jar")) {
//                    System.out.println(entryIn.getName());
                    String s = new String(toByteArray);
//                    System.out.println();
//                    System.out.println();
//                    System.out.println();
//                    System.out.println();
                    if (archiveDate != null) {
                        try {
                            s = falsifyString(entryIn.getName(), toByteArray, s, archiveDate);
                        } catch (Exception ee) {
                            ee.printStackTrace();
                            throw new RuntimeException("falsifyString exception!", ee);
                        }
                    }
//                    if (!entryIn.getName().endsWith("changelog.json")) {
//                        System.out.println(s);
//                    }
//                    System.out.println();
//                    System.out.println();
//                    System.out.println();
//                    System.out.println();
                    ZipEntry zipEntry = new ZipEntry(entryIn.getName());
                    if (archiveDate != null) {
                        zipEntry.setTime(archiveDate.getTime());
                    }
                    zos.putNextEntry(zipEntry);
                    zos.write(s.getBytes(), 0, s.getBytes().length);
                } else {
                    if (archiveDate != null) {
                        entryIn.setTime(archiveDate.getTime());
                    }
                    zos.putNextEntry(entryIn);
                    zos.write(toByteArray, 0, toByteArray.length);
                }
                zos.closeEntry();
            }
            zos.close();
            zipFile.close();
            file.delete();
            file2.renameTo(file);
            changeFileChangeDate(file, archiveDate);
        }
    }
    static Pattern builtBy = Pattern.compile("(Built-By: )(.+?)([\\s\n$])");
    static Pattern buildJdk = Pattern.compile("(Build-Jdk: )(.+?)([\\s\n$])");
    static Pattern maven = Pattern.compile("(#Generated by Maven).*?\n(.+?)\n", Pattern.DOTALL);
    static Pattern buildTimestamp = Pattern.compile("(build.timestamp=)(.+?\\s.+?)([\\s\n$])");
    static Pattern buildTime = Pattern.compile("(Build-Time: )(.+?\\s.+?)([\\s\n$])");
    static Pattern json = Pattern.compile("^[.+]$", Pattern.DOTALL);
    static Pattern comments = Pattern.compile("(<!--)(.+?)(-->)");

    private String falsifyString(String fileName, byte[] toByteArray, String string, Date archiveDate) throws Exception {
        //        string = string.replaceAll("Built-By: IBS_ERZL", "Built-By: IBS");
//        string = string.replaceAll("Build-Jdk: 1.8.0_102", "Build-Jdk: 1.8.0");
//        string = string.replaceAll("(#Generated by Maven.+\\n).+?\\n", "$1");
        string = applyChange(string, builtBy, "IBS");
        string = applyChange(string, buildJdk, "1.8.0");
        string = applyChange(string, maven, "\n#" + new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", new Locale("en", "EN")).format(archiveDate), "\n");
        string = applyChange(string, buildTimestamp, new SimpleDateFormat("dd-MM-yyyy HH:mm").format(archiveDate));
        string = applyChange(string, buildTime, new SimpleDateFormat("dd-MM-yyyy HH:mm").format(archiveDate));
        try {
            string = applyChange(string, comments, "");
        } catch (Exception e) {
            e.printStackTrace();
            string = applyChange(string, comments, "");
        }
        if (json.matcher(string).matches() || fileName.endsWith("changelog.json")) {
            getGitLogs(toByteArray);
            string = new String(jsonBytes, "utf-8");
        }
        return string;
    }

    private String applyChange(String string, Pattern pattern, String valueToChange, String valueToSet) {
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            string = matcher.replaceAll("$1" + valueToChange + valueToSet);
        }
        return string;
    }

    private String applyChange(String string, Pattern pattern, String valueToChange) {
        return applyChange(string, pattern, valueToChange, "$3");
    }

    protected void copyFiles(String targetModulesDir, String tomcatModulesDir, boolean falsify) throws Exception {
        for (File file : new File(targetModulesDir).listFiles()) {
            if (!file.isDirectory()) {
                copyOneFile(tomcatModulesDir, file, falsify);
            } else {
                String targetDir = tomcatModulesDir + s + file.getName();
                recreateTargetDir(targetDir);
                copyFiles(file.getAbsolutePath(), targetDir, falsify);
            }
        }
        System.out.println("Modules updated!");
    }

    protected void recreateTargetDir(String targetDir) throws Exception {
        File dir = new File(targetDir);
        if (dir.exists()) {
            FileUtils.deleteDirectory(dir);
        }
        dir.mkdirs();
    }

    protected void copyOneFile(String tomcatModulesDir, File file, boolean falsify) throws Exception {
        if (falsify) {
            checkArchiveFiles(file);
        }
        setFileModificationDate(file);
        File targetFile = new File(tomcatModulesDir + s + file.getName());
        Files.copy(file.toPath(), targetFile.toPath(), REPLACE_EXISTING);
        System.out.println(file.getAbsolutePath() + " --> copied to --> " + targetFile.getAbsolutePath());
    }

    protected void getPmpClientFromServer(String tomcatModulesDir, String pmpClientPath, String sshPmpClientPath, String host, String user, String password) {
        File pmpClient = new File(tomcatModulesDir + pmpClientPath);
        if (pmpClient.exists()) {
            pmpClient.delete();
        }
        Scp.scpFrom(host, 22, user, password, sshPmpClientPath, pmpClient.getPath());
        if (pmpClient.exists()) {
            System.out.println("PmpClient updated successfuly!");
        } else {
            System.out.println("PmpClient update failed!");
        }
    }

    protected void deleteDir(String dirStr) throws IOException {
        File dir = new File(dirStr);
        if (dir.exists()) {
            FileUtils.deleteDirectory(dir);
            System.out.println("Dir Deleted: " + dir.getAbsolutePath());
        }
    }

    protected void deleteDirs(String dirStr) throws IOException {
        File dir = new File(dirStr);
        if (dir.exists()) {
            if (dir.listFiles().length > 0) {
                for (File subdir : dir.listFiles()) {
                    deleteDir(subdir.getAbsolutePath());
                }
                System.out.println("Dirs Deleted: " + dir.getAbsolutePath());
            }
        }
    }

    protected void deleteFiles(String dirStr) {
        File dir = new File(dirStr);
        if (dir.exists()) {
            if (dir.listFiles().length > 0) {
                for (File file : dir.listFiles()) {
                    if (!file.isDirectory()) {
                        file.delete();
                    }
                }
                System.out.println("Files Deleted: " + dir.getAbsolutePath());
            }
        }
    }

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getZipArchPath() {
        return zipArchPath;
    }

    public String getTargetUnpackDir() {
        return targetUnpackDir;
    }

    public String getTomcatModulesDir() {
        return tomcatModulesDir;
    }

    public String getModules() {
        return modules;
    }

    public String getPmpClientPath() {
        return pmpClientPath;
    }

    public String getSshPmpClientPath() {
        return sshPmpClientPath;
    }

    public String[] getDeleteDirPath() {
        return deleteDirPath;
    }

    public String[] getDeleteFilesPath() {
        return deleteFilesPath;
    }

    public String[] getDeleteDirsPath() {
        return deleteDirsPath;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public boolean isTargetSystemIsLinux() {
        return targetSystemIsLinux;
    }

    public void setTargetSystemIsLinux(boolean targetSystemIsLinux) {
        this.targetSystemIsLinux = targetSystemIsLinux;
    }

    public boolean isForceCopy() {
        return forceCopy;
    }

    public void setForceCopy(boolean forceCopy) {
        this.forceCopy = forceCopy;
    }

    public Properties getProperties() {
        return properties;
    }

}
