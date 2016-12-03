import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import miner.model.domain.Project;
import miner.util.exception.ValidationException;

public class GitExtractorTest {
		
	public static void main(String args[]) throws Exception {
		Project project = new Project(7770259,"openejb","https://github.com/apache/openejb");
		getBranchesProject(project);
		//downloadBranch(null);
		getCommits();		
	}
	
	public static void getBranchesProject(Project project)
			throws Exception {
		Collection<Ref> remoteRefs = Git.lsRemoteRepository().setHeads(false).setTags(true).setRemote(project.getUrl()).call();
		Repository r = Git.lsRemoteRepository().setHeads(false).setTags(true).setRemote(project.getUrl()).getRepository();
		RevWalk walk = new RevWalk(r);
		for (Ref ref : remoteRefs) {
			RevTag tag = walk.parseTag(ref.getObjectId());
		    System.out.println(tag.getTaggerIdent().getWhen());			
		}
	}
		
	public static void getCommits() throws IOException,GitAPIException,ValidationException {
		List<String> commits = new ArrayList<String>();
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File("C:/Mestrado/downloads/openejb/trunk/.git")).readEnvironment().findGitDir().build();
		
		List<Ref> call = new Git(repository).tagList().call();
		RevWalk walk = new RevWalk(repository);
		for (Ref ref : call) {			
			RevTag tag = walk.parseTag(ref.getObjectId());
		    //System.out.println(tag.getTaggerIdent().getWhen());
		}				
	}

}
