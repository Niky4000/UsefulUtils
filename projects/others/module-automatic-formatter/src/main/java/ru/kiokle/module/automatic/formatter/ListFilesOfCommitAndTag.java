package ru.kiokle.module.automatic.formatter;

import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import static org.openjdk.tools.javac.tree.TreeInfo.args;

/**
 * Simple snippet which shows how to get a list of files/directories based on a
 * specific commit or a tag.
 */
public class ListFilesOfCommitAndTag {

//    public static void main(String[] args) throws IOException {
//        try (Repository repository = CookbookHelper.openJGitCookbookRepository()) {
//            List<String> paths = readElementsAt(repository, "6409ee1597a53c6fbee31edf9cde31dc3afbe20f", "src/main/java/org/dstadler/jgit/porcelain");
//
//            System.out.println("Had paths for commit: " + paths);
//
//            final ObjectId testbranch = repository.resolve("testbranch");
//            paths = readElementsAt(repository, testbranch.getName(), "src/main/java/org/dstadler/jgit/porcelain");
//
//            System.out.println("Had paths for tag: " + paths);
//        }
//    }
    public static List<String> check(Git r, RevCommit commitToCheck) throws IOException, GitAPIException, JGitInternalException {
        List<String> changedFiles = new ArrayList<>();
        try (RevWalk rw = new RevWalk(r.getRepository())) {
//			RevCommit commitToCheck = (RevCommit) rw.parseAny(r.getRepository().resolve(args[1]));
//			System.out.println("Inspecting commit " + args[1] + " with id:" + commitToCheck.getId() + " in repo "+r.getRepository().getDirectory());
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

    public static void check2(Git r, RevCommit commitToCheck, Ref branch) throws IOException, GitAPIException, JGitInternalException {
        try (RevWalk rw = new RevWalk(r.getRepository())) {

            if (branch != null && rw.isMergedInto(commitToCheck, rw.parseCommit(branch.getObjectId()))) {

//			RevCommit commitToCheck = (RevCommit) rw.parseAny(r.getRepository().resolve(args[1]));
//			System.out.println("Inspecting commit " + args[1] + " with id:" + commitToCheck.getId() + " in repo "+r.getRepository().getDirectory());
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
                        }
                    }
                }
            }
        }
    }

    public static List<String> readElementsAt(Repository repository, RevCommit commit, String path) throws IOException {
//        RevCommit revCommit = buildRevCommit(repository, commit);

        // and using commit's tree find the path
        RevTree tree = commit.getTree();
        //System.out.println("Having tree: " + tree + " for commit " + commit);

        List<String> items = new ArrayList<>();

        // shortcut for root-path
        if (path.isEmpty()) {
            try (TreeWalk treeWalk = new TreeWalk(repository)) {
                treeWalk.addTree(tree);
                treeWalk.setRecursive(true);
                treeWalk.setPostOrderTraversal(false);
                treeWalk.setFilter(TreeFilter.ANY_DIFF);

                while (treeWalk.next()) {
                    items.add(treeWalk.getPathString());
                }
            }
        } else {
            // now try to find a specific file
            try (TreeWalk treeWalk = buildTreeWalk(repository, tree, path)) {
                if ((treeWalk.getFileMode(0).getBits() & FileMode.TYPE_TREE) == 0) {
                    throw new IllegalStateException("Tried to read the elements of a non-tree for commit '" + commit.getName() + "' and path '" + path + "', had filemode " + treeWalk.getFileMode(0).getBits());
                }

                try (TreeWalk dirWalk = new TreeWalk(repository)) {
                    dirWalk.addTree(treeWalk.getObjectId(0));
                    dirWalk.setRecursive(false);
                    while (dirWalk.next()) {
                        items.add(dirWalk.getPathString());
                    }
                }
            }
        }

        return items;
    }

    private static RevCommit buildRevCommit(Repository repository, String commit) throws IOException {
        // a RevWalk allows to walk over commits based on some filtering that is defined
        try (RevWalk revWalk = new RevWalk(repository)) {
            return revWalk.parseCommit(ObjectId.fromString(commit));
        }
    }

    private static TreeWalk buildTreeWalk(Repository repository, RevTree tree, final String path) throws IOException {
        TreeWalk treeWalk = TreeWalk.forPath(repository, path, tree);

        if (treeWalk == null) {
            throw new FileNotFoundException("Did not find expected file '" + path + "' in tree '" + tree.getName() + "'");
        }

        return treeWalk;
    }
}
