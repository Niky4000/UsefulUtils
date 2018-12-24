package ru.kiokle.module.automatic.formatter;

import com.google.common.collect.ImmutableSet;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.google.googlejavaformat.java.JavaFormatterOptions;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.JschSession;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.RemoteSession;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.FS;

/**
 * @author NAnishhenko
 */
public class StartFormat {

    public static final String REPOSITORY_PATH = "D:\\GIT\\pmp";
    private static final String AUTHOR_TO_CHECK = "NikitaAnischenko";
    private static final String FORMATTING_COMMIT_MESSAGE = "# Formatting...";
    public static final Set<String> IGNORE_PATH_SET = ImmutableSet.<String>builder()
//            .add("ru/ibs/pmp/api/model/dbf/moparcel")
//            .add("ru/ibs/pmp/api/model/dbf/parcel")
            .add("pmp/module-pmp-bill-recreate")
            .add("pmp/module-pmp-bill-recreate-executor")
            .build();

    static SshSessionFactory sshSessionFactory = createSshSessionFactory();

    public static void main(String[] args) throws Exception {
//        getSessionX();
        EclipseFormatterClass eclipseFormatterClass = new EclipseFormatterClass();
        eclipseFormatterClass.format();
//        File repositoryDir = new File(REPOSITORY_PATH);
//        getLastLogs2(repositoryDir);
    }

    public static void getLastLogs2(File repositoryDir) throws IOException, GitAPIException, Exception {
        System.out.println("-------------------------------");
        System.out.println("-------------------------------");
        System.out.println("-------------------------------");
        System.out.println("-------------------------------");
        Git git = Git.open(new File(repositoryDir.getAbsolutePath() + "/.git"));
//        git.push().setRemote("NAnishhenko@ibs.ru:pagekeeper@http://172.29.4.20:8084/mgfoms/pmp.git").call();
        String fullBranch = git.getRepository().getFullBranch();
        Ref currentBrunch = git.getRepository().exactRef(fullBranch);
        git.getRepository().getFS().setUserHome(new File("D:\\Users\\NAnishhenko\\.ssh"));
//        push(repositoryDir, currentBrunch);
        Iterable<RevCommit> logs = git.log().setRevFilter(RevFilter.NO_MERGES).setMaxCount(40).call();
        Set<String> check = new HashSet<>();
        for (RevCommit commit : logs) {
            System.out.println("LogCommit: " + commit + " Author: " + commit.getAuthorIdent().getName() + " Message: " + commit.getFullMessage());
            if (commit.getFullMessage().equals(FORMATTING_COMMIT_MESSAGE)) {
                break;
            }
            if (!commit.getAuthorIdent().getName().equals(AUTHOR_TO_CHECK)) {
                continue;
            }
            check.addAll(check(git, commit));

            System.out.println("-------------------------------");
            System.out.println("-------------------------------");
            System.out.println("-------------------------------");
            System.out.println("-------------------------------");

        }

        List<String> filteredCheckList = check.stream().filter(str -> IGNORE_PATH_SET.stream().noneMatch(path -> str.startsWith(path))).collect(Collectors.toList());
        if (!filteredCheckList.isEmpty()) {
            for (String checkedFile : filteredCheckList) {
                File file = new File(repositoryDir.getAbsolutePath() + "/" + checkedFile);
                String formattedFile = commitFormat(file.getAbsolutePath());
                Files.write(file.toPath(), formattedFile.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            }
            Set<String> changedFiles = git.status().call().getChanged();
            if (!changedFiles.isEmpty()) {
                commit(repositoryDir, changedFiles, FORMATTING_COMMIT_MESSAGE);
                push(repositoryDir, currentBrunch);
            }
        }

        git.close();
    }

    private static Session getSessionX() throws JSchException {
//        String host = uri.getHost();
//                    int port = uri.getPort();
        JSch jsch = new JSch();
        String user = "pmp";
        String host = "172.29.4.20";
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
        return session;
    }

    public static List<String> check(Git r, RevCommit commitToCheck) throws IOException, GitAPIException, JGitInternalException {
        List<String> changedFiles = new ArrayList<>();
        try (RevWalk rw = new RevWalk(r.getRepository())) {
            try (TreeWalk tw = new TreeWalk(r.getRepository())) {
                tw.setRecursive(true);
                tw.addTree(commitToCheck.getTree());
                // tw.setFilter(TreeFilter.ANY_DIFF);
                for (RevCommit parent : commitToCheck.getParents()) {
                    rw.parseCommit(parent);
                    System.out.println("Adding parent commit " + parent.getId());
                    tw.addTree(parent.getTree());
                }
                while (tw.next()) {
                    int similarParents = 0;
                    for (int i = 1; i < tw.getTreeCount(); i++) {
                        if (tw.getFileMode(i) == tw.getFileMode(0) && tw.getObjectId(0).equals(tw.getObjectId(i))) {
                            similarParents++;
                        }
                    }
                    if (similarParents == 0) {
                        System.out.println("modified path:" + tw.getPathString());
                        changedFiles.add(tw.getPathString());
                    }
                }
            }
        }
        return changedFiles;
    }

    private static void commit(File repositoryDir, Collection<String> changes, String commitMessage) throws GitAPIException, NoWorkTreeException, IOException {
        Git git = Git.open(new File(repositoryDir.getAbsolutePath() + "/.git"));
        CommitCommand commit = git.commit();
        for (String path : changes) {
            commit.setOnly(path);
        }
        RevCommit call = commit.setMessage(commitMessage).call();
        System.out.println(commitMessage);
    }

    private static void push(File repositoryDir, Ref ref) throws IOException, GitAPIException {
        Git git = Git.open(new File(repositoryDir.getAbsolutePath() + "/.git"));
        Iterable<PushResult> call = git.push()
                //                .setRemote("NAnishhenko@ibs.ru:pagekeeper@http://172.29.4.20:8084/mgfoms/pmp.git")
                .setRemote("http://172.29.4.20:8084/mgfoms/pmp.git")
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider("NAnishhenko@ibs.ru", "pagekeeper"))
                //                .setTransportConfigCallback(new TransportConfigCallback() {
                //                    @Override
                //                    public void configure(Transport transport) {
                //                        if (transport instanceof SshTransport) {
                //                            SshTransport sshTransport = (SshTransport) transport;
                //                            sshTransport.setSshSessionFactory(sshSessionFactory);
                //                        }
                //                    }
                //                })
                .add(ref).call();
        Iterator<PushResult> iterator = call.iterator();
        while (iterator.hasNext()) {
            PushResult next = iterator.next();
            Collection<RemoteRefUpdate> remoteUpdates = next.getRemoteUpdates();
            for (RemoteRefUpdate update : remoteUpdates) {
                System.out.println(update.getStatus().toString());
            }
        }
    }

    private static String commitFormat(String fileName) throws FormatterException, Exception, IOException {
//        String fileName = "D:\\GIT\\UsefulUtils\\projects\\others\\module-automatic-formatter\\src\\main\\java\\ru\\kiokle\\module\\automatic\\formatter\\debug\\ClassForTest.java";
//        String fileName = "D:\\GIT\\pmp\\pmp\\module-pmp-bill-recreate-executor\\src\\main\\java\\ru\\ibs\\pmp\\module\\recreate\\exec\\ServiceExecuteDbKeeperAspect.java";
        JavaFormatterOptions options = JavaFormatterOptions.builder().style(JavaFormatterOptions.Style.AOSP).build();
        String formatSource = new Formatter(options).formatSource(new String(Files.readAllBytes(new File(fileName).toPath())));
        return formatSource;
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

                    String user = "git"; //git@172.29.4.20:mgfoms/pmp.git
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
}
