package miner.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import miner.model.domain.Branch;
import miner.model.domain.Commit;
import miner.model.domain.CommitChange;
import miner.model.domain.Project;
import miner.util.exception.ValidationException;

public class GitExtractor {

	public static void downloadBranch(Branch branch)
			throws TransportException, InvalidRemoteException, GitAPIException {
		Git.cloneRepository().setURI(branch.getProject().getUrl())
				.setDirectory(new File(branch.getLocalPathDownloads())).setBranch(branch.getName()).call();
	}

	public static List<Branch> getBranchesProject(Project project)
			throws TransportException, InvalidRemoteException, GitAPIException {
		Collection<Ref> remoteRefs = Git.lsRemoteRepository().setHeads(true).setRemote(project.getUrl()).call();
		Log.writeLog("Return " + remoteRefs);
		List<Branch> branches = new ArrayList<>();
		for (Ref ref : remoteRefs) {
			String branch = ref.getName().replace("refs/heads/", "");
			branches.add(new Branch(branch, project));
		}
		return branches;
	}

	public static List<Commit> getCommits(Branch branch) throws IOException,GitAPIException,ValidationException {
		List<Commit> commits = new ArrayList<Commit>();
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repo = builder.setGitDir(new File(branch.getGitDir())).build();
		
		Git git = new Git(repo);		
		final RevWalk walk = new RevWalk(repo);
		walk.sort(RevSort.REVERSE);
		walk.markStart(walk.parseCommit(repo.resolve(Constants.HEAD)));
		RevTree oldRevTree = null;

		for (RevCommit revCommit : walk) {

			Commit commit = new Commit(revCommit.getName(), revCommit.getAuthorIdent().getName(),
					revCommit.getAuthorIdent().getEmailAddress(), revCommit.getAuthorIdent().getWhen(),
					revCommit.getCommitterIdent().getName(), revCommit.getCommitterIdent().getEmailAddress(),
					revCommit.getCommitterIdent().getWhen(), revCommit.getFullMessage(), revCommit.getShortMessage(),
					branch);
			commits.add(commit);
			
			Log.writeLog("Looking for changes in Commit " + commit.getHash());
			commit.setChanges(getChanges(git,revCommit,oldRevTree,commit));
			Log.writeLog("Find " + commit.getChanges().size() + " changes in Commit " + commit.getHash());
			oldRevTree = revCommit.getTree();						
		}
		walk.close();
		git.close();
		if (commits.isEmpty()) {
			Log.writeLog("Cant found commits in branch " + branch.getName());
			throw new ValidationException("Cant found commits in branch " + branch.getName());
		}
		return commits;
	}

	private static List<CommitChange> getChanges(Git git, RevCommit revCommit,RevTree oldRevTree,Commit commit) 
			throws IOException,GitAPIException {
		List<CommitChange> changes = new ArrayList<>();
		
		RevTree revTree = revCommit.getTree();
		ObjectReader reader = git.getRepository().newObjectReader();		
		CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
		CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();		
		newTreeIter.reset(reader, revTree.getId());
		
		if (oldRevTree != null) {											
    		oldTreeIter.reset(reader, oldRevTree.getId());	    			    		
		} 
		
		List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
		for (DiffEntry entry : diffs) {
			byte[] sourceCode = null;
			if (entry.getChangeType() != ChangeType.DELETE) {
				ObjectLoader loader = git.getRepository().open(entry.getNewId().toObjectId());				
				InputStream in = loader.openStream();
				sourceCode = IOUtils.toByteArray(in);
			}
			CommitChange change = new CommitChange(entry.getChangeType(), entry.getOldPath(), 
					                               entry.getNewPath(), sourceCode,commit);
			changes.add(change);
		}	    			    					
		return changes;
	}

	public static void main(String args[]) throws Exception {
		Project project = new Project();
		project.setName("JHotDraw");
		project.setUrl("https://github.com/wumpz/jhotdraw.git");
		Log.writeLog(project.getName(), "TestGitExtractor", "Starting Test Git Extractor " + project.getName());
		Branch branch = new Branch("master", project);
		System.out.println(getCommits(branch));
	}

}

//String command = "git clone -b " + branch.getName() + " " + branch.getProject().getUrl() + " "
//+ branch.getLocalPathDownloads();
//Log.writeLog("Exec git command to download branch local " + command);

//String command = "git ls-remote --heads " + project.getUrl();
//Log.writeLog("Exec git command to get all branch names " + command);


//String command[] = { "/bin/sh", "-c",
//"git --git-dir=" + branch.getLocalPathDownloads() + "/.git --work-tree="
//		+ branch.getLocalPathDownloads()
//		+ " log --reverse --pretty=format:%H###%h###%cn###%ce###%ct###%s " + branch.getName() };
//Log.writeLog("Exec git command to get all commits " + Arrays.toString(command));

//treeWalk.close();
			// if (oldRevTree != null) {
			// treeWalk = new TreeWalk(repo);
			// treeWalk.addTree(revTree);
			// treeWalk.addTree(oldRevTree);
			// treeWalk.setRecursive(true);
			// List<DiffEntry> l = DiffEntry.scan(treeWalk);
			// for (DiffEntry diff : l) {
			// Log.writeLog(diff.getOldPath());
			// Log.writeLog(diff.getNewPath());
			// Log.writeLog(diff.getChangeType().toString());
			// Log.writeLog(diff.getOldId().toString());
			// Log.writeLog(diff.getNewId().toString());
			// }
			// RenameDetector rd = new RenameDetector(repo);
			// rd.addAll(DiffEntry.scan(treeWalk));
			//
			// List<DiffEntry> lde = rd.compute(treeWalk.getObjectReader(),
			// null);
			// for (DiffEntry de : lde) {
			// if (de.getScore() >= rd.getRenameScore()) {
			// System.out.println("file: " + de.getOldPath() + " copied/moved
			// to: " + de.getNewPath());
			// }
			// }
			// }


//Iterable<RevCommit> log = git.log().call();

	// for (RevCommit revCommit : log) {
//	TreeWalk treeWalk = new TreeWalk(repo);
//	treeWalk.addTree(revTree);
//	treeWalk.setRecursive(true);
//	// System.out.println(treeWalk.getTreeCount());
//	while (treeWalk.next()) {
//		//Log.writeLog("PathString: " + treeWalk.getPathString());
//		// System.out.println("NameString: " +
//		// treeWalk.getNameString());
//		ObjectId object = treeWalk.getObjectId(0);
//		ObjectLoader loader = repo.open(object);
//		InputStream in = loader.openStream();
//		String myString = IOUtils.toString(in, "UTF-8");
//		// System.out.println(myString);
//	}			
	

//	public static List<CommitChange> getChanges(Commit commit) throws Exception {
//		String command = "git --git-dir=" + commit.getBranch().getLocalPathDownloads() + "/.git --work-tree="
//				+ commit.getBranch().getLocalPathDownloads() + " show --pretty=format: --name-status "
//				+ commit.getHash();
//		Log.writeLog("Exec git command to get files changed in commit " + commit.getHash());
//		Process powerShellProcess = Runtime.getRuntime().exec(command);
//		powerShellProcess.getOutputStream().close();
//		List<CommitChange> changes = new ArrayList<>();
//		String line;
//		BufferedReader stdout = new BufferedReader(new InputStreamReader(powerShellProcess.getInputStream()));
//		while ((line = stdout.readLine()) != null) {
//			if (!line.equals("")) {
//				Log.writeLog("Return " + line);
//				String[] values = line.split("\t");
//				String modificationType = values[0];
//				String fileName = values[1];
//				changes.add(new CommitChange(FileModificationTypeEnum.getType(modificationType), fileName, commit));
//			}
//		}
//		return changes;
//	}
//
//	public static byte[] getSourceCode(CommitChange change) throws IOException {
//		String command = "git --git-dir=" + change.getCommit().getBranch().getLocalPathDownloads()
//				+ "/.git --work-tree=" + change.getCommit().getBranch().getLocalPathDownloads() + " show "
//				+ change.getCommit().getHash() + ":" + change.getFileName();
//		Log.writeLog("Exec git command to get source code in file " + change.getFileName());
//		Process powerShellProcess = Runtime.getRuntime().exec(command);
//		powerShellProcess.getOutputStream().close();
//		String line;
//		BufferedReader stdout = new BufferedReader(new InputStreamReader(powerShellProcess.getInputStream()));
//		StringBuilder sb = new StringBuilder("");
//		while ((line = stdout.readLine()) != null) {
//			sb.append(line).append("\n");
//		}
//		return sb.toString().getBytes();
//	}



//package miner.model.domain;
//
//import org.eclipse.jgit.api.Git;
//import org.eclipse.jgit.api.errors.GitAPIException;
//import org.eclipse.jgit.diff.DiffEntry;
//import org.eclipse.jgit.diff.RenameDetector;
//import org.eclipse.jgit.errors.MissingObjectException;
//import org.eclipse.jgit.lib.Repository;
//import org.eclipse.jgit.revwalk.RevCommit;
//import org.eclipse.jgit.treewalk.TreeWalk;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;

/**
 * Create a Log command that enables the follow option: git log --follow -- < path >
 * User: OneWorld
 * Example for usage: ArrayList<RevCommit> commits =  new  LogFollowCommand(repo,"src/com/mycompany/myfile.java").call();
 */
//public class LogFollowCommand {
//
//    private final Repository repository;
//    private String path;
//    private Git git;
//
//    /**
//     * Create a Log command that enables the follow option: git log --follow -- < path >
//     * @param repository
//     * @param path
//     */
//    public LogFollowCommand(Repository repository, String path){
//        this.repository = repository;
//        this.path = path;
//    }
//
//    /**
//     * Returns the result of a git log --follow -- < path >
//     * @return
//     * @throws IOException
//     * @throws MissingObjectException
//     * @throws GitAPIException
//     */
//    public ArrayList<RevCommit> call() throws IOException, MissingObjectException, GitAPIException {
//        ArrayList<RevCommit> commits = new ArrayList<RevCommit>();
//        git = new Git(repository);
//        RevCommit start = null;
//        do {
//            Iterable<RevCommit> log = git.log().addPath(path).call();
//            for (RevCommit commit : log) {
//                if (commits.contains(commit)) {
//                    start = null;
//                } else {
//                    start = commit;
//                    commits.add(commit);
//                }
//            }
//            if (start == null) return commits;
//        }
//        while ((path = getRenamedPath( start)) != null);
//
//        return commits;
//    }
//
//    /**
//     * Checks for renames in history of a certain file. Returns null, if no rename was found.
//     * Can take some seconds, especially if nothing is found... Here might be some tweaking necessary or the LogFollowCommand must be run in a thread.
//     * @param start
//     * @return String or null
//     * @throws IOException
//     * @throws MissingObjectException
//     * @throws GitAPIException
//     */
//    private String getRenamedPath( RevCommit start) throws IOException, MissingObjectException, GitAPIException {
//        Iterable<RevCommit> allCommitsLater = git.log().add(start).call();
//        for (RevCommit commit : allCommitsLater) {
//
//            TreeWalk tw = new TreeWalk(repository);
//            tw.addTree(commit.getTree());
//            tw.addTree(start.getTree());
//            tw.setRecursive(true);
//            RenameDetector rd = new RenameDetector(repository);
//            rd.addAll(DiffEntry.scan(tw));
//            List<DiffEntry> files = rd.compute();
//            for (DiffEntry diffEntry : files) {
//                if ((diffEntry.getChangeType() == DiffEntry.ChangeType.RENAME || diffEntry.getChangeType() == DiffEntry.ChangeType.COPY) && diffEntry.getNewPath().contains(path)) {
//                    System.out.println("Found: " + diffEntry.toString() + " return " + diffEntry.getOldPath());
//                    return diffEntry.getOldPath();
//                }
//            }
//        }
//        return null;
//    }
//}
