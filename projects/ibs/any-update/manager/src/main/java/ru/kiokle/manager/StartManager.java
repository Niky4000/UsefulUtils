/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.manager;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.JschSession;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.RemoteSession;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FS;

/**
 *
 * @author me
 */
public class StartManager {

    // Локальное переключение
    // JAVA_HOME=/usr/lib/jvm/jdk1.8.0_261
    // java -jar /home/me/GIT/UsefulUtils/projects/ibs/any-update/manager/target/manager.jar -mvn /home/me/netbeans-12.1/netbeans/java/maven/bin/mvn -build /home/me/GIT/pmp/pmp_core,/home/me/GIT/pmp/pmp,/home/me/GIT/pmp/pmp/module-pmp-bill-recreate
    // Переключение на 26-м
    // java -jar manager.jar -cp1 /home/pump/manager/profiles/build-local.properties,/home/pump/tmp/pmp/pmp/profiles/build-local.properties -git /home/pump/tmp/pmp -url https://git.drzsrv.ru/mgfoms/pmp.git -checkout -pull -branch test-recreator -u NAnishhenko -p pagekeeper -clone -mvn /home/pump/manager/maven/bin/mvn -build /home/pump/tmp/pmp/pmp_core,/home/pump/tmp/pmp/pmp,/home/pump/tmp/pmp/pmp/module-pmp-bill-recreate
    // Переключение на 116-м
    // java -jar C:\GIT\UsefulUtils\projects\ibs\any-update\manager\target\manager.jar -mvn "C:\Program Files\NetBeans-12.1\netbeans\java\maven\bin\mvn.cmd" -build C:\GIT\pmp\pmp_core,C:\GIT\pmp\pmp,C:\GIT\pmp\pmp\module-pmp-bill-recreate
    public static void main(String[] args) throws Exception {
        System.out.println("Hello World!!! -----------------------------------------------------------------------");
        System.out.println("Hello World!!! -----------------------------------------------------------------------");
        System.out.println("Hello World!!! -----------------------------------------------------------------------");
        System.out.println("Hello World!!! -----------------------------------------------------------------------");
        List<String> argList = Arrays.asList(args);
        if (argList.contains("-git") && argList.contains("-url") && argList.contains("-branch") && argList.contains("-u") && argList.contains("-p")) {
            File git = new File(getArg(argList, "-git"));
            if (argList.contains("-clone")) {
                if (git.exists()) {
                    FileUtils.deleteDirectory(git);
                }
                git.mkdirs();
//        gitClone(git, null, "https://git.drzsrv.ru/mgfoms/pmp.git");
                gitClone2(git, getArg(argList, "-url"), getArg(argList, "-branch"), getArg(argList, "-u"), getArg(argList, "-p"));
                if (argList.contains("-cp1")) {
                    copyFile(getArg(argList, "-cp1").split(","));
                }
            }
            if (argList.contains("-checkout") && argList.contains("-branch")) {
                checkout(git, getArg(argList, "-branch"));
            }
            if (argList.contains("-pull")) {
                gitPull(git, null, getArg(argList, "-branch"), MergeStrategy.RECURSIVE, getArg(argList, "-u"), getArg(argList, "-p"));
            }
        }
//            executeMavenInstall("/home/me/netbeans-11.3/netbeans/java/maven/bin/mvn", "/home/me/GIT/pmp/pmp_core");
//            executeMavenInstall("/home/me/netbeans-11.3/netbeans/java/maven/bin/mvn", "/home/me/GIT/pmp/pmp");
//            executeMavenInstall("/home/me/netbeans-11.3/netbeans/java/maven/bin/mvn", "/home/me/GIT/pmp/pmp/module-pmp-bill-recreate");
        if (argList.contains("-mvn") && argList.contains("-build")) {
            String buildStr = getArg(argList, "-build");
            for (String build : buildStr.split(",")) {
                executeMavenInstall(getArg(argList, "-mvn"), build);
            }
        }

        System.out.println();
        System.out.println("Finished!!! ---------------------------------------------------------------------------");
        System.out.println("Finished!!! ---------------------------------------------------------------------------");
        System.out.println("Finished!!! ---------------------------------------------------------------------------");
        System.out.println("Finished!!! ---------------------------------------------------------------------------");
    }

    private static void copyFile(String[] files) throws IOException {
        Files.copy(new File(files[0]).toPath(), new File(files[1]).toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private static String getArg(List<String> argList, String argument) {
        return argList.get(argList.indexOf(argument) + 1);
    }

    private static void executeMavenInstall(String mavenPath, String projectToBuild) throws InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec(mavenPath + " -DskipTests clean install", null, new File(projectToBuild));
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            // Read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
            // Read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static MergeResult gitPull(File file, SshSessionFactory sshSessionFactory, String remoteBranchName, MergeStrategy stategy, String user, String password) throws IOException, GitAPIException {
        PullCommand pull = Git.open(new File(file.getAbsolutePath() + "/.git")).pull();
//        pull.setTransportConfigCallback(new TransportConfigCallback() {
//            @Override
//            public void configure(Transport transport) {
//                SshTransport sshTransport = (SshTransport) transport;
//                sshTransport.setSshSessionFactory(sshSessionFactory);
//            }
//        });
        pull.setCredentialsProvider(new org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider(user, password));
        if (remoteBranchName != null) {
//            pull.setRemoteBranchName("refs/heads/" + remoteBranchName);
            pull.setRemoteBranchName(remoteBranchName);
            pull.setRemote(".");
        }
        if (stategy != null) {
            pull.setStrategy(stategy);
        }
        PullResult result = pull.call();
        FetchResult fetchResult = result.getFetchResult();
        MergeResult mergeResult = result.getMergeResult();
        MergeResult.MergeStatus mergeStatus = mergeResult.getMergeStatus(); // this should be interesting
        System.out.println("gitPull: mergeStatus = " + mergeStatus.toString());
        return mergeResult;
    }

    private static void gitClone2(File file, String repositoryURI, String branch, String user, String password) throws GitAPIException {
        CloneCommand clone = Git.cloneRepository()
                //                .setURI("https://git.drzsrv.ru/mgfoms/pmp.git")
                .setURI(repositoryURI)
                .setDirectory(file)
                //                .setBranch("develop")
                .setBranch(branch)
                .setProgressMonitor(new org.eclipse.jgit.lib.ProgressMonitor() {
                    @Override
                    public void start(int i) {
                        System.out.println("start " + i + "!");
                    }

                    @Override
                    public void beginTask(String string, int i) {
                        System.out.println("beginTask " + string + " " + i + "!");
                    }

                    @Override
                    public void update(int i) {
//                        System.out.println("update " + i + "!");
                    }

                    @Override
                    public void endTask() {
                        System.out.println("endTask!");
                    }

                    @Override
                    public boolean isCancelled() {
                        return false;
                    }
                })
                .setCredentialsProvider(new org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider(user, password));

        clone.call();
    }

    private static void gitClone(File file, SshSessionFactory sshSessionFactory, String repositoryURI) throws GitAPIException {
        CloneCommand cloneCommand = Git.cloneRepository();
        cloneCommand.setURI(repositoryURI);
//        cloneCommand.setURI("pmp@172.29.4.20:pmp");
        cloneCommand.setDirectory(file);
        cloneCommand.setCredentialsProvider(new org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider("NAnishhenko", "pagekeeper"));
//        cloneCommand.setTransportConfigCallback(new TransportConfigCallback() {
//            @Override
//            public void configure(Transport transport) {
//                SshTransport sshTransport = (SshTransport) transport;
//                sshTransport.setSshSessionFactory(sshSessionFactory);
//            }
//        });
        Git call = cloneCommand.call();
        System.out.println("gitClone finished!");
//        LogCommand log = call.log();
//        Iterable<RevCommit> logCall = log.call();
//        Iterator<RevCommit> iterator = logCall.iterator();
//        while (iterator.hasNext()) {
//            RevCommit next = iterator.next();
//            System.out.println(next.toString());
//        }
    }

    private static SshSessionFactory createSshSessionFactory() {
        SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
            }

            @Override
            protected JSch createDefaultJSch(FS fs) throws JSchException {
                JSch defaultJSch = super.createDefaultJSch(fs);
                defaultJSch.addIdentity("D:\\GitKeys3\\putty_private.ppk", "pagekeeper");
                return defaultJSch;
            }

            @Override
            public synchronized RemoteSession getSession(URIish uri,
                    CredentialsProvider credentialsProvider, FS fs, int tms)
                    throws TransportException {
                try {
                    String host = uri.getHost();
//                    int port = uri.getPort();

                    JSch jsch = new JSch();

                    String user = "pmp";
//                String host = "172.29.4.20";
                    int port = 22;
                    String privateKey = ".ssh/id_rsa";

                    jsch.addIdentity("D:\\GitKeys3\\id_dsa", "pagekeeper");
                    System.out.println("identity added ");

                    Session session = jsch.getSession(user, host, port);
                    System.out.println("session created.");

                    java.util.Properties config = new java.util.Properties();
                    config.put("StrictHostKeyChecking", "no");
                    session.setConfig(config);

                    session.connect();
                    return new JschSession(session, uri);
//                execCommand(session.openChannel("exec"), "pwd");
//                session.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

            }

        };
        return sshSessionFactory;
    }

    private static Ref checkout(File repositoryDir, String branchName) throws GitAPIException, NoWorkTreeException, IOException {
        Git git = Git.open(new File(repositoryDir.getAbsolutePath() + "/.git"));
//        Ref call = git.checkout().setAllPaths(true).setName(name).call();
        Ref ref = git.checkout().
                setCreateBranch(!branchExistsLocally(git, "refs/heads/" + branchName)).
                setName(branchName).
                setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).
                setStartPoint("origin/" + branchName).
                call();
        System.out.println("Checkouted to " + branchName);
        return ref;
    }

    private static boolean branchExistsLocally(Git git, String remoteBranch) throws GitAPIException {
        List<Ref> branches = git.branchList().call();
        List<String> branchNames = branches.stream().map(Ref::getName).collect(Collectors.toList());
        return branchNames.contains(remoteBranch);
    }
}
