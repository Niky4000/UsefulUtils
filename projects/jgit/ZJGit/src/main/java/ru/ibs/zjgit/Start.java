package ru.ibs.zjgit;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheCheckout;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.internal.JGitText;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.JschSession;
import org.eclipse.jgit.transport.RemoteSession;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FS;
import com.google.common.collect.Collections2;
import com.google.common.base.Function;
import java.util.Iterator;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.dircache.DirCacheIterator;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.merge.Merger;
import org.eclipse.jgit.merge.StrategyResolve;
import org.eclipse.jgit.merge.ThreeWayMerger;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.WorkingTreeIterator;
import org.eclipse.jgit.treewalk.filter.PathFilter;

/**
 * @author NAnishhenko
 */
public class Start {

    static SshSessionFactory sshSessionFactory = createSshSessionFactory();
    private static final String _REC = "_rec";
    private static final String _DEV = "_dev";

    public static void main(String[] args) throws Exception {
        System.out.println("Hello World!");

        String masterName = "master";
//        String developName = "refs/remotes/origin/develop";
//        String developName = "remotes/origin/develop";
        String developName = "develop";

//        File file = new File("D:\\tmp\\testrepo");
        File file = new File("D:\\GIT\\pmp");
//
//        gitClone(file, sshSessionFactory, "pmp@172.29.4.20:pmp");

        handleHotfix(file, developName, masterName, MergeStrategy.RECURSIVE);
//        actAfterMergeConflicts(file, null, masterName, null, developName, null, MergeStrategy.RECURSIVE);
//        makeFinalCommit(file, null, null, developName, masterName, null);
//        handleConflicts(file);
//        handleCommit(file);
//        revert(file);
//        tests(file);
//
//
//        String currentBranchName = "hotfix-assembly2";
//        String currentBranchNameDev = currentBranchName + "_dev";
//        checkoutToLocal(file, currentBranchName);
//        MergeResult pullToMasterResult = gitPull(file, sshSessionFactory, masterName);
//        String hello="";
    }

    private static void handleHotfix(File file, String developName, String masterName, MergeStrategy strategy) throws NoWorkTreeException, IOException, GitAPIException, InterruptedException {
        String currentBranchName = getCurrentBranchName(file);
        if (!currentBranchName.startsWith("hotfix")) {
            throw new RuntimeException("Wrong branch!");
        }
        String currentBranchNameRecovery = currentBranchName + _REC;
        createBranch(file, currentBranchNameRecovery);
        String currentBranchVersion = getVersionNumber(file);
        checkout(file, developName);
        MergeResult developPull = gitPull(file, sshSessionFactory, null, strategy);
        if (developPull.getMergeStatus().equals(MergeResult.MergeStatus.CONFLICTING)) {
            throw new RuntimeException("developPull conflicts!");
        }
        String developVersionNumber = getVersionNumber(file);
        checkout(file, masterName);
        MergeResult masterPull = gitPull(file, sshSessionFactory, null, strategy);
        if (masterPull.getMergeStatus().equals(MergeResult.MergeStatus.CONFLICTING)) {
            throw new RuntimeException("masterPull conflicts!");
        }
        String masterVersionNumber = getVersionNumber(file);

        checkoutToLocal(file, currentBranchName);

        String currentBranchNameDev = currentBranchName + _DEV;
        createBranch(file, currentBranchNameDev);

        checkoutToLocal(file, currentBranchNameDev);
        MergeResult pullToDevelopResult = gitPull(file, sshSessionFactory, developName, strategy);

        if (pullToDevelopResult.getMergeStatus().equals(MergeResult.MergeStatus.CONFLICTING)) {
            throw new RuntimeException("Merge Conflicts!!!");
        }

        String versionNumber = setMvnVersion(masterVersionNumber, developVersionNumber, currentBranchVersion, file, currentBranchNameDev, false);

        actAfterMergeConflicts(file, currentBranchName, masterName, versionNumber, developName, currentBranchNameDev, strategy);
    }

    private static void actAfterMergeConflicts(File file, String currentBranchName, String masterName, String versionNumber, String developName, String currentBranchNameDev, MergeStrategy strategy) throws IOException, GitAPIException, NoWorkTreeException, RuntimeException, InterruptedException {
        if (currentBranchName == null && versionNumber == null && currentBranchNameDev == null) {
            currentBranchNameDev = getCurrentBranchName(file);
            versionNumber = getVersionNumber(file);
            currentBranchName = currentBranchNameDev.replace(_DEV, "");
        }
        checkoutToLocal(file, currentBranchName);
        MergeResult pullToMasterResult = gitPull(file, sshSessionFactory, masterName, strategy);
        if (pullToMasterResult.getMergeStatus().equals(MergeResult.MergeStatus.CONFLICTING)) {
            throw new RuntimeException("Pull from develop conflicts!");
        }

        setMvnVersion(versionNumber, versionNumber, versionNumber, file, currentBranchName, true);

        checkout(file, masterName);
        MergeResult masterMergeResult = gitMerge(file, currentBranchName);
//                    MergeResult pullToMasterResult = gitPull(file, sshSessionFactory, currentBranchName);
        checkout(file, developName);
        MergeResult developMergeResult = gitMerge(file, currentBranchNameDev);
//                    MergeResult pullToDevelopResult = gitPull(file, sshSessionFactory, currentBranchName);

        if (masterMergeResult.getMergeStatus().equals(MergeResult.MergeStatus.CONFLICTING) || developMergeResult.getMergeStatus().equals(MergeResult.MergeStatus.CONFLICTING)) {
            throw new RuntimeException("Pull from master conflicts!");
        }

        makeFinalCommit(file, versionNumber, currentBranchName, developName, masterName, currentBranchNameDev);
    }

    private static void makeFinalCommit(File file, String versionNumber, String currentBranchName, String developName, String masterName, String currentBranchNameDev) throws IOException, GitAPIException, NoWorkTreeException {
        if (currentBranchName == null && versionNumber == null && currentBranchNameDev == null) {
            String currentBranchName_ = getCurrentBranchName(file);
            if (currentBranchName_.contains("hotfix") && currentBranchName_.endsWith(_DEV)) {
                currentBranchNameDev = currentBranchName_;
                versionNumber = getVersionNumber(file);
                currentBranchName = currentBranchNameDev.replace(_DEV, "");
            } else if (currentBranchName_.contains("hotfix")) {
                currentBranchNameDev = currentBranchName_ + _DEV;
                versionNumber = getVersionNumber(file);
                currentBranchName = currentBranchName_;
            }
        }
        // I need to debug it!!!
        checkout(file, masterName);
        Ref tagAddingResult = addTag(file, versionNumber, getTagMessage(currentBranchName));
        push(file, tagAddingResult);
        Ref developRef = checkout(file, developName);
        push(file, developRef);
        Ref masterRef = checkout(file, masterName);
        push(file, masterRef);
// I need to debug it!!!

        String currentBranchNameRec = currentBranchName + _REC;
        deleteBranch(file, currentBranchNameDev);
//        deleteBranch(file, currentBranchName);
        deleteBranch(file, currentBranchNameRec);

        System.out.println("Success!");
    }

    private static String setMvnVersion(String masterVersionNumber, String developVersionNumber, String currentBranchVersion, File file, String currentBranchName, boolean doNotIncrement) throws NoWorkTreeException, InterruptedException, RuntimeException, GitAPIException, IOException {
        String versionNumber = getMaxVersionNumber(masterVersionNumber, developVersionNumber, currentBranchVersion, doNotIncrement);
        checkoutToLocal(file, currentBranchName);
        boolean mvnSetNewVersion = mvnSetNewVersion(file, versionNumber);
        if (!mvnSetNewVersion) {
            throw new RuntimeException("mvnSetNewVersion failure!");
        }
        boolean onlyPomXmlsWereChanged = true;
        Set<String> changes = getChanges(file);
        for (String path : changes) {
            if (!new File(path).getName().equals("pom.xml")) {
                onlyPomXmlsWereChanged = false;
            }
        }
        if (!onlyPomXmlsWereChanged) {
            throw new RuntimeException("Non only pom.xml files were changed!");
        }
        if (!changes.isEmpty()) {
            commit(file, changes, "Set version to " + versionNumber);
        }
        return versionNumber;
    }

    private static void revert(File repositoryDir) throws Exception {
        Git git = Git.open(new File(repositoryDir.getAbsolutePath() + "/.git"));
        String currentBranchNameRec = getCurrentBranchName(repositoryDir);
        if (!currentBranchNameRec.endsWith(_REC)) {
            throw new RuntimeException("Not a rec repository!");
        }
        String currentBranchName = currentBranchNameRec.replace(_REC, "");
        String currentBranchNameDev = currentBranchName + _DEV;
        deleteBranch(repositoryDir, currentBranchNameDev);
        deleteBranch(repositoryDir, currentBranchName);
        createBranch(repositoryDir, currentBranchName);
        checkoutToLocal(repositoryDir, currentBranchName);
        deleteBranch(repositoryDir, currentBranchNameRec);
    }

    private static void tests(File repositoryDir) throws Exception {
        Git git = Git.open(new File(repositoryDir.getAbsolutePath() + "/.git"));
        Merger merger = MergeStrategy.OURS.newMerger(git.getRepository());

    }

    private static void handleCommit(File repositoryDir) throws Exception {
        Git git = Git.open(new File(repositoryDir.getAbsolutePath() + "/.git"));
        Set<String> conflicting = git.status().call().getConflicting();
        Repository repository = git.getRepository();

        // find the HEAD
        ObjectId lastCommitId = repository.resolve(Constants.HEAD);

        for (String conflict : conflicting) {
            // a RevWalk allows to walk over commits based on some filtering that is defined
            RevWalk revWalk = new RevWalk(repository);
            try {
                RevCommit commit = revWalk.parseCommit(lastCommitId);
                // and using commit's tree find the path
                RevTree tree = commit.getTree();
                System.out.println("Having tree: " + tree);

                // now try to find a specific file
                TreeWalk treeWalk = new TreeWalk(repository);
                try {
                    treeWalk.addTree(tree);
                    treeWalk.setRecursive(true);
                    DirCache dirCache = repository.readDirCache();
                    treeWalk.addTree(new DirCacheIterator(dirCache));

                    treeWalk.setFilter(PathFilter.create(conflict));

//                treeWalk.setFilter(PathFilter.ANY_DIFF);
                    if (!treeWalk.next()) {
                        throw new IllegalStateException("No more files'");
                    }

//                treeWalk.
//                ObjectId objectId = treeWalk.getObjectId(0);
//                ObjectLoader loader = repository.open(objectId);
                    // and then one can the loader to read the file
//                loader.copyTo(System.out);
                    AbstractTreeIterator treeIterator = treeWalk.getTree(0,
                            AbstractTreeIterator.class);
                    DirCacheIterator dirCacheIterator = treeWalk.getTree(1,
                            DirCacheIterator.class);
//                    WorkingTreeIterator workingTreeIterator = treeWalk.getTree(2,
//                            WorkingTreeIterator.class);

                    if (dirCacheIterator != null) {
                        final DirCacheEntry dirCacheEntry = dirCacheIterator
                                .getDirCacheEntry();
                        if (dirCacheEntry != null) {
                            int stage = dirCacheEntry.getStage();
                            FileMode fileMode = dirCacheEntry.getFileMode();
                            String toString = fileMode.toString();
                            if (stage > 0) {
                                String path = treeWalk.getPathString();
//                                addConflict(path, stage);
                                dirCacheEntry.setFileMode(FileMode.REGULAR_FILE);
                                continue;
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                revWalk.dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void push(File repositoryDir, Ref ref) throws IOException, GitAPIException {
        Git git = Git.open(new File(repositoryDir.getAbsolutePath() + "/.git"));
        Iterable<PushResult> call = git.push().setTransportConfigCallback(new TransportConfigCallback() {
            @Override
            public void configure(Transport transport) {
                SshTransport sshTransport = (SshTransport) transport;
                sshTransport.setSshSessionFactory(sshSessionFactory);
            }
        }).add(ref).call();
        Iterator<PushResult> iterator = call.iterator();
        while (iterator.hasNext()) {
            PushResult next = iterator.next();
            Collection<RemoteRefUpdate> remoteUpdates = next.getRemoteUpdates();
            for (RemoteRefUpdate update : remoteUpdates) {
                System.out.println(update.getStatus().toString());
            }
        }
    }

    private static String getTagMessage(String branchName) {
        return "#" + branchName.replace("hotfix-", "");
    }

    private static Ref addTag(File repositoryDir, String tagName, String tagMessage) throws IOException, GitAPIException {
        Git git = Git.open(new File(repositoryDir.getAbsolutePath() + "/.git"));
        Ref tagRef = git.getRepository().getTags().get(tagName);
        if (tagRef == null) {
            tagRef = git.tag().setName(tagName).setMessage(tagMessage).call();
        }
        return tagRef;
    }

    private static Ref createBranch(File repositoryDir, String newBranchName) throws IOException, GitAPIException {
        Git git = Git.open(new File(repositoryDir.getAbsolutePath() + "/.git"));
        Ref call = git.branchCreate().setName(newBranchName).call();
        return call;
    }

    private static List<String> deleteBranch(File repositoryDir, String branchName) throws IOException, GitAPIException {
        Git git = Git.open(new File(repositoryDir.getAbsolutePath() + "/.git"));
        List<String> call = git.branchDelete().setBranchNames(branchName).setForce(true).call();
        return call;
    }

    private static String getCurrentBranchName(File repositoryDir) throws IOException {
        Git git = Git.open(new File(repositoryDir.getAbsolutePath() + "/.git"));
        String branch = git.getRepository().getBranch();
        return branch;
    }

    private static String getMaxVersionNumber(String versionMaster, String versionDevelop, String currentBranchVersion, boolean doNotIncrement) throws IOException {
        Integer numMaster = Integer.valueOf(versionMaster.substring(versionMaster.lastIndexOf(".") + 1));
        Integer numDevelop = Integer.valueOf(versionDevelop.substring(versionDevelop.lastIndexOf(".") + 1));
        Integer numCurrent = Integer.valueOf(currentBranchVersion.substring(currentBranchVersion.lastIndexOf(".") + 1));
        Integer preMax = Integer.valueOf(Math.max(numMaster, numDevelop));
        Integer max = Integer.valueOf(Math.max(preMax, numCurrent));
        if (doNotIncrement) {
            max++;
        }
        String ret = versionMaster.substring(0, versionMaster.lastIndexOf(".") + 1) + max.toString();
        return ret;
    }

    private static String getVersionNumber(File repositoryDir) throws IOException {
        File moduleXml = new File(repositoryDir.getAbsolutePath() + "/pmp/pom.xml");
        String moduleXmlContent = new String(Files.readAllBytes(moduleXml.toPath()));
        String version = moduleXmlContent.substring(moduleXmlContent.indexOf("<version>") + "<version>".length(), moduleXmlContent.indexOf("</version>"));
        Integer num = Integer.valueOf(version.substring(version.lastIndexOf(".") + 1));
        String ret = version.substring(0, version.lastIndexOf(".") + 1) + num.toString();
        return ret;
    }

    private static void listAllBranches(File file) throws IOException, GitAPIException {
        //        gitRevertAll(file, changes);
//
//        boolean mvnSetNewVersion = mvnSetNewVersion(file, "1.170515.32");
//        Set<String> changes = getChanges(file);
//        commit(file, changes);

        Git git = Git.open(new File(file.getAbsolutePath() + "/.git"));
        List<Ref> call = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
        for (Ref ref : call) {
            System.out.println(ref.getName());
        }
    }

    private static boolean mvnSetNewVersion(File repositoryDir, String version) throws IOException, InterruptedException {
        String executeString = "C:\\apache-maven-3.3.3\\bin\\mvn.cmd -f " + repositoryDir.getAbsolutePath() + "\\pmp" + " versions:set -DnewVersion=" + version;
        String executeString2 = "C:\\apache-maven-3.3.3\\bin\\mvn.cmd -f " + repositoryDir.getAbsolutePath() + "\\pmp" + " versions:commit";
        String env = System.getenv("JAVA_HOME");
        Process process = Runtime.getRuntime().exec(executeString, new String[]{"M2_HOME=C:\\apache-maven-3.3.3", "JAVA_HOME=C:\\Program Files\\Java\\jre1.8.0_112"});
        String processData = new String(IOUtils.toByteArray(process.getInputStream()));
        process.waitFor();
        Process process2 = Runtime.getRuntime().exec(executeString2, new String[]{"M2_HOME=C:\\apache-maven-3.3.3", "JAVA_HOME=C:\\Program Files\\Java\\jre1.8.0_112"});
        String processData2 = new String(IOUtils.toByteArray(process2.getInputStream()));
        process2.waitFor();
        if (processData.contains("SUCCESS") && processData2.contains("SUCCESS")) {
            return true;
        } else {
            return false;
        }
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

    private static void checkoutToLocal(File repositoryDir, String branchName) throws GitAPIException, NoWorkTreeException, IOException {
        Git git = Git.open(new File(repositoryDir.getAbsolutePath() + "/.git"));
//        Ref call = git.checkout().setAllPaths(true).setName(name).call();
//        Ref ref = git.checkout().
//                setCreateBranch(!branchExistsLocally(git, "refs/heads/" + branchName)).
//                setName(branchName).
//                setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).
//                setStartPoint("refs/heads/" + branchName).
//                call();
//        Ref thisBranch = git.getRepository().getAllRefs().get(branchName);
//        String name = thisBranch.getName();
//        List<Ref> branchList = git.branchList().call();
//        Ref ref = git.checkout().
//                setCreateBranch(!branchExistsLocally(git, "refs/heads/" + branchName)).
//                setName(branchName).
//                setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).
//                setStartPoint("refs/heads/" + branchName).
//                call();
        Ref call = git.checkout().setName("refs/heads/" + branchName).call();
    }

    private static MergeResult gitMerge(File file, String remoteBranchName) throws IOException, GitAPIException {
        Git git = Git.open(new File(file.getAbsolutePath() + "/.git"));
        Repository repository = git.getRepository();
        MergeCommand merge = git.merge();
        merge.setFastForward(MergeCommand.FastForwardMode.NO_FF);
        Ref ref = repository.getRef("refs/heads/" + remoteBranchName);
        merge.include(ref);
        merge.setStrategy(MergeStrategy.SIMPLE_TWO_WAY_IN_CORE); // ??? I need to debug it!!!
        MergeResult result = merge.call();
        MergeResult.MergeStatus mergeStatus = result.getMergeStatus(); // this should be interesting
        System.out.println("gitMerge: mergeStatus = " + mergeStatus.toString());
        return result;
    }

    private static boolean branchExistsLocally(Git git, String remoteBranch) throws GitAPIException {
        List<Ref> branches = git.branchList().call();
        Collection<String> branchNames = Collections2.transform(branches, new Function<Ref, String>() {

            @Override
            public String apply(Ref input) {
                return input.getName();
            }

        });
        return branchNames.contains(remoteBranch);
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

    private static MergeResult handleConflicts(File repositoryDir) throws GitAPIException, NoWorkTreeException, IOException {
        Git git = Git.open(new File(repositoryDir.getAbsolutePath() + "/.git"));
        Set<String> conflicting = git.status().call().getConflicting();
        if (!conflicting.isEmpty()) {
            boolean onlyXmls = true;
            for (String conflict : conflicting) {
                if (!new File(conflict).getName().endsWith(".xml")) {
                    onlyXmls = false;
                }
                System.out.println(conflict);
            }
            if (onlyXmls) {
                Repository repository = git.getRepository();
                ObjectId resolve = git.getRepository().resolve(Constants.HEAD);
                ObjectId commit1 = git.getRepository().resolve("ca9d171402aae2aa03d1da3664b74e794dc60727");
//                ObjectId commit2=git.getRepository().resolve("ff9a65be7f2a15aa042c39aae4e06ca63a2ae940");
//                ObjectId commit3=git.getRepository().resolve("d1a18f0f46b0d1255a4acf400665df23683b5f48");
//                ObjectId resolve = git.getRepository().resolve("HEAD");
//                String branch = git.getRepository().getBranch();
//                Ref ref = git.getRepository().resgetRef(branch);
//                MergeResult call = git.merge().include(resolve).setStrategy(MergeStrategy.THEIRS).call();
//                    Repository db = git.getRepository();
//                Merger ourMerger = MergeStrategy.THEIRS.newMerger(repository);
//                boolean merge = ourMerger.merge(new ObjectId[]{resolve, commit1});
//                StrategyResolve strategyResolve = new StrategyResolve();
//                ThreeWayMerger merger = strategyResolve.newMerger(repository, true);
//                merger.setBase(resolve);
//                boolean merge = merger.merge(resolve, commit1);

                String branchName = git.getRepository().getBranch();
                Ref call = git.checkout().setName("refs/heads/" + branchName).setStartPoint("HEAD^").addPath("pmp/module-system-auth/pom.xml").call();

                return null;
            } else {
                throw new RuntimeException("Not only xmls conflicted!");
            }
        } else {
            System.out.println("No conflicts!");
        }
        return null;
    }

    private static void deleteRepository(File repositoryDir) throws IOException {
        if (repositoryDir.exists()) {
            FileUtils.deleteDirectory(repositoryDir);
        }
        repositoryDir.mkdirs();
        System.out.println("Repository deleted!");
    }

    public static Set<String> getChanges(File repositoryDir) throws IOException, GitAPIException {
        Git git = Git.open(new File(repositoryDir.getAbsolutePath() + "/.git"));
        Set<String> changed = git.status().call().getChanged();
        Set<String> added = git.status().call().getAdded();
        Set<String> ignoredNotInIndex = git.status().call().getIgnoredNotInIndex();
        Set<String> missing = git.status().call().getMissing();
        Set<String> modified = git.status().call().getModified();
        Set<String> removed = git.status().call().getRemoved();
        Set<String> untracked = git.status().call().getUntracked();
        Set<String> untrackedFolders = git.status().call().getUntrackedFolders();
        Set<String> uncommittedChanges = git.status().call().getUncommittedChanges();
//        Repository repository = git.getRepository();
//        DirCache readDirCache = repository.readDirCache();
//        final ObjectId commitId = resolveRefToCommitId(repository);
//        final ObjectId commitTree = parseCommit(repository, commitId).getTree();
//        ObjectReader objectReader = repository.getObjectDatabase().newReader();
//        DirCache dc = repository.lockDirCache();
//        try {
//            for (String path : uncommittedChanges) {
//                DirCacheEntry entry = dc.getEntry(path);
//                DirCacheCheckout.checkoutEntry(repository, new File(repositoryDir.getAbsolutePath(), path), entry, objectReader);
//            }
//        } finally {
//            dc.unlock();
//        }
        return uncommittedChanges;
    }

    private static void gitRevertAll(File repositoryDir, Collection<String> filesToRevert) throws IOException, GitAPIException {
        Git git = Git.open(new File(repositoryDir.getAbsolutePath() + "/.git"));
        Repository repository = git.getRepository();
        ObjectReader objectReader = repository.getObjectDatabase().newReader();
        DirCache dc = repository.lockDirCache();
        try {
            for (String path : filesToRevert) {
                DirCacheEntry entry = dc.getEntry(path);
                DirCacheCheckout.checkoutEntry(repository, new File(repositoryDir.getAbsolutePath(), path), entry, objectReader);
            }
        } finally {
            dc.unlock();
        }
    }

    private static String getRefOrHEAD() {
        return "HEAD";
    }

    private static ObjectId resolveRefToCommitId(Repository repo) {
        try {
            return repo.resolve(getRefOrHEAD() + "^{commit}"); //$NON-NLS-1$
        } catch (IOException e) {
            throw new JGitInternalException(
                    MessageFormat.format(JGitText.get().cannotRead, getRefOrHEAD()),
                    e);
        }
    }

    private static RevCommit parseCommit(Repository repo, final ObjectId commitId) {
        RevCommit commit;
        RevWalk rw = new RevWalk(repo);
        try {
            commit = rw.parseCommit(commitId);
        } catch (IOException e) {
            throw new JGitInternalException(MessageFormat.format(
                    JGitText.get().cannotReadCommit, commitId.toString()), e);
        } finally {
            rw.release();
        }
        return commit;
    }

    private void checkoutIndex(Repository repo, ObjectId commitTree) throws IOException,
            GitAPIException {
        DirCache dc = repo.lockDirCache();
        try {
            DirCacheCheckout checkout = new DirCacheCheckout(repo, dc,
                    commitTree);
            checkout.setFailOnConflict(false);
            try {
                checkout.checkout();
            } catch (org.eclipse.jgit.errors.CheckoutConflictException cce) {
                throw new CheckoutConflictException(checkout.getConflicts(),
                        cce);
            }
        } finally {
            dc.unlock();
        }
    }

    private static MergeResult gitPull(File file, SshSessionFactory sshSessionFactory, String remoteBranchName, MergeStrategy stategy) throws IOException, GitAPIException {
        PullCommand pull = Git.open(new File(file.getAbsolutePath() + "/.git")).pull();
        pull.setTransportConfigCallback(new TransportConfigCallback() {
            @Override
            public void configure(Transport transport) {
                SshTransport sshTransport = (SshTransport) transport;
                sshTransport.setSshSessionFactory(sshSessionFactory);
            }
        });
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

    private static void gitClone(File file, SshSessionFactory sshSessionFactory, String repositoryURI) throws GitAPIException {
        CloneCommand cloneCommand = Git.cloneRepository();
        cloneCommand.setURI(repositoryURI);
//        cloneCommand.setURI("pmp@172.29.4.20:pmp");
        cloneCommand.setDirectory(file);
        cloneCommand.setTransportConfigCallback(new TransportConfigCallback() {
            @Override
            public void configure(Transport transport) {
                SshTransport sshTransport = (SshTransport) transport;
                sshTransport.setSshSessionFactory(sshSessionFactory);
            }
        });
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
            protected void configure(Host host, Session session) {
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

    private static void execCommand(Channel channel, String command) throws JSchException, IOException {
        ((com.jcraft.jsch.ChannelExec) channel).setCommand(command);
        channel.setInputStream(null);
        ((com.jcraft.jsch.ChannelExec) channel).setErrStream(System.err);
        InputStream in = channel.getInputStream();
        channel.connect();
        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) {
                    break;
                }
                System.out.print(new String(tmp, 0, i));
            }
            if (channel.isClosed()) {
                if (in.available() > 0) {
                    continue;
                }
                System.out.println("exit-status: " + channel.getExitStatus());
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        channel.disconnect();
    }
}
