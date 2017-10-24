package ru.ibs.tomcatrestart;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author NAnishhenko
 */
public class TomcatRestartRemote extends TomcatRestart {

    final Integer port;
    final String serverName;
    final String launchScript;
    final String launchScriptPath;

    public TomcatRestartRemote(String configFileName) throws Exception {
        super(configFileName);
        port = Integer.valueOf(getProperties().getProperty("port"));
        serverName = getProperties().getProperty("serverName");
        launchScript = getProperties().getProperty("launchScript").replace("\\n", "\n");
        launchScriptPath = getProperties().getProperty("launchScriptPath");
    }

    @Override
    protected void initActions() {
        if (isTargetSystemIsLinux()) {
            try {
                Scp.createSshClient(getHost(), getUser(), getPassword(), port, new String[]{"mkdir -p " + getTomcatModulesDir()});
                String output = Scp.createSshClient(getHost(), getUser(), getPassword(), port, new String[]{"ps -eF | grep java"});
                String[] split = output.split("\n");
                String targetOutputLine = null;
                for (String str : split) {
                    if (str.contains(serverName)) {
                        targetOutputLine = str;
                        break;
                    }
                }
                if (targetOutputLine != null) {
                    Matcher matcher = Pattern.compile("^[\\w]+?[^\\w]+?([\\w]+?)[^\\w].+$", Pattern.DOTALL).matcher(targetOutputLine);
                    if (matcher.find()) {
                        String processIdStr = matcher.group(1);
                        Integer processId = Integer.valueOf(processIdStr);
                        String killProcessOutput = Scp.createSshClient(getHost(), getUser(), getPassword(), port, new String[]{"kill -9 " + processId.toString()});
                        System.out.println(processId.toString() + " killed!");
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(TomcatRestartRemote.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    protected void postActions() {
        if (isTargetSystemIsLinux()) {
            try {
                File file = new File("launchScript.sh");
                Files.write(file.toPath(), launchScript.getBytes(), StandardOpenOption.CREATE_NEW);
                Scp.scpTo(getHost(), port, getUser(), getPassword(), launchScriptPath + getS() + file.getName(), file.getAbsolutePath());
                file.delete();
                String output = Scp.createSshClient(getHost(), getUser(), getPassword(), port, new String[]{"sh " + launchScriptPath + getS() + file.getName()});
                Scp.createSshClient(getHost(), getUser(), getPassword(), port, new String[]{"rm -rf " + launchScriptPath + getS() + file.getName()});
            } catch (Exception ex) {
                Logger.getLogger(TomcatRestartRemote.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    protected void deleteFiles(String dirStr) {
        delete(dirStr);
    }

    @Override
    protected void deleteDirs(String dirStr) throws IOException {
        delete(dirStr);
    }

    @Override
    protected void deleteDir(String dirStr) throws IOException {
        delete(dirStr);
    }

    private void delete(String path) {
        try {
            Scp.createSshClient(getHost(), getUser(), getPassword(), port, new String[]{path});
        } catch (Exception ex) {
            Logger.getLogger(TomcatRestartRemote.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void getPmpClientFromServer(String tomcatModulesDir, String pmpClientPath, String sshPmpClientPath, String host, String user, String password) {
    }
    
    @Override
    protected void recreateTargetDir(String targetDir) throws Exception {
        Scp.createSshClient(getHost(), getUser(), getPassword(), port, new String[]{"rm -rf " + targetDir});
        Scp.createSshClient(getHost(), getUser(), getPassword(), port, new String[]{"mkdir -p " + targetDir});
    }

    @Override
    protected void copyOneFile(String tomcatModulesDir, File file) throws IOException {
        Scp.scpTo(getHost(), port, getUser(), getPassword(), tomcatModulesDir + getS() + file.getName(), file.getAbsolutePath());
        System.out.println(file.getAbsolutePath() + " --> copied to --> " + tomcatModulesDir + getS() + file.getName());
    }

}
