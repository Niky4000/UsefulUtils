package ru.kiokle.module.automatic.formatter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

/**
 * @author NAnishhenko
 */
public class OtherTrials {

    static void setFinalStatic(Object obj, Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(obj, newValue);
    }

    private static String simpleFileBrowser(RevCommit commit, Repository repository) {
        String out = new String();
        try {
            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(new RevWalk(repository).parseTree(commit));

            while (treeWalk.next()) {
                out += "--- /dev/null\n";
                out += "+++ b/" + treeWalk.getPathString() + "\n";
//			out+= "+"+BlobUtils.getContent(repository, commit,treeWalk.getPathString().replace("\n", "\n+"));
                out += "\n";
            }
        } finally {
            return out;
        }
    }

    public static void getLastLogs(File repositoryDir) throws IOException, GitAPIException {
        Git git = Git.open(new File(repositoryDir.getAbsolutePath() + "/.git"));
        Repository repo = git.getRepository();

        RevWalk walk = new RevWalk(repo);

        List<Ref> branches = git.branchList().call();

        for (Ref branch : branches) {
            String branchName = branch.getName();

            System.out.println("Commits of branch: " + branch.getName());
            System.out.println("-------------------------------------");

            Iterable<RevCommit> commits = git.log().all().call();

            for (RevCommit commit : commits) {
                boolean foundInThisBranch = false;

                RevCommit targetCommit = walk.parseCommit(repo.resolve(
                        commit.getName()));
                for (Map.Entry<String, Ref> e : repo.getAllRefs().entrySet()) {
                    if (e.getKey().startsWith(Constants.R_HEADS)) {
                        if (walk.isMergedInto(targetCommit, walk.parseCommit(
                                e.getValue().getObjectId()))) {
                            String foundInBranch = e.getValue().getName();
                            if (branchName.equals(foundInBranch)) {
                                foundInThisBranch = true;
                                break;
                            }
                        }
                    }
                }

                if (foundInThisBranch) {
                    System.out.println(commit.getName());
                    System.out.println(commit.getAuthorIdent().getName());
                    System.out.println(new Date(commit.getCommitTime() * 1000L));
                    System.out.println(commit.getFullMessage());
                }
            }
        }
    }

}
