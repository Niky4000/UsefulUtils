package ru.kiokle.module.automatic.formatter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jgit.api.Git;
import org.osgi.framework.Bundle;
import static ru.kiokle.module.automatic.formatter.StartFormat.IGNORE_PATH_SET;

/**
 * @author NAnishhenko
 */
public class EclipseFormatterClass {

    private static final String CONFIG_PATH = "D:\\GIT\\pmp\\tools\\ide_settings\\eclipse-formatter.xml";
//    private static final String CONFIG_PATH = "D:\\tmp\\eclipse-formatter.xml";

    public void format() throws Exception {
        File repositoryDir = new File(StartFormat.REPOSITORY_PATH);
        Git git = Git.open(new File(repositoryDir.getAbsolutePath() + "/.git"));
        Set<String> changed = git.status().call().getChanged();
        Set<String> added = git.status().call().getAdded();
        Set<String> modified = git.status().call().getModified();
        Set<String> uncommitted = git.status().call().getUncommittedChanges();
        Set<String> changedFiles = new HashSet<>(changed.size() + added.size() + modified.size() + uncommitted.size());
        changedFiles.addAll(changed);
        changedFiles.addAll(added);
        changedFiles.addAll(modified);
        changedFiles.addAll(uncommitted);
        List<File> filteredChangedList = changedFiles.stream().filter(str -> str.endsWith(".java")).filter(str -> IGNORE_PATH_SET.stream().noneMatch(path -> str.contains(path))).map(fileStr -> new File(repositoryDir.getAbsolutePath() + "/" + fileStr)).collect(Collectors.toList());
        for (File fileStr : filteredChangedList) {
            System.out.println(fileStr.getAbsolutePath());
        }
        System.out.println("----------------------");
        System.out.println("----------------------");
        System.out.println("----------------------");
        System.out.println("----------------------");
        format(filteredChangedList);
        // For debug!
//        format(Arrays.asList(new File("D:\\GIT\\pmp\\pmp\\module-pmp-api\\src\\main\\java\\ru\\ibs\\pmp\\api\\model\\Bill.java"),
//                 new File("D:\\GIT\\pmp\\pmp\\module-pmp-api\\src\\main\\java\\ru\\ibs\\pmp\\api\\model\\dbf\\moparcel\\InvoiceRecordWithoutOnkology.java")
//        ));
        // For debug!
    }

    private void format(List<File> fileList) throws Exception {
        String[] args_ = new String[]{"-verbose", "-config", CONFIG_PATH};
        String[] args = new String[args_.length + fileList.size()];
        for (int i = 0; i < args_.length; i++) {
            args[i] = args_[i];
        }
        for (int i = 0; i < fileList.size(); i++) {
            args[i + args_.length] = fileList.get(i).getAbsolutePath();
        }
        final Map<String, String[]> argsMap = new HashMap<>();
        argsMap.put(IApplicationContext.APPLICATION_ARGS, args);

        IApplicationContext context = new IApplicationContext() {
            @Override
            public Map getArguments() {
                return argsMap;
            }

            @Override
            public void applicationRunning() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public String getBrandingApplication() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public String getBrandingName() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public String getBrandingDescription() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public String getBrandingId() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public String getBrandingProperty(String string) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Bundle getBrandingBundle() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setResult(Object o, IApplication ia) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        CodeFormatterApplication2 codeFormatterApplication2 = new CodeFormatterApplication2();
        codeFormatterApplication2.start(context);
        for (File file : fileList) {
            byte[] readAllBytes = Files.readAllBytes(file.toPath());
            byte[] letterI = new byte[]{(byte) 0xD0, 0x3F};
            byte[] letterI2 = new byte[]{(byte) 0xD0, (byte) 0x98};
            boolean needToUpdateFile = false;
            for (int i = 1; i < readAllBytes.length; i++) {
                if (readAllBytes[i - 1] == letterI[0] && readAllBytes[i] == letterI[1]) {
                    readAllBytes[i] = letterI2[1];
                    needToUpdateFile = true;
                }
            }
            if (needToUpdateFile) {
                Files.write(file.toPath(), readAllBytes, StandardOpenOption.TRUNCATE_EXISTING);
            }
        }
    }

}
