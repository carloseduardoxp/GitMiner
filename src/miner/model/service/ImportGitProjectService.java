/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miner.model.service;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import miner.model.dao.BranchDao;
import miner.model.dao.CommitChangeDao;
import miner.model.dao.CommitDao;
import miner.model.dao.ProjectDao;
import miner.model.dao.structure.DaoFactory;
import miner.model.dao.structure.JdbcConnection;
import miner.model.domain.Branch;
import miner.model.domain.Commit;
import miner.model.domain.Project;
import miner.util.GitExtractor;
import miner.util.Log;
import miner.util.exception.ValidationException;

/**
 *
 * @author carloseduardoxp
 */
public class ImportGitProjectService {

    private final ProjectDao projectDao;
    private final BranchDao branchDao;
    private final CommitDao commitDao;
    private final CommitChangeDao commitChangeDao;
    private final Observer observer;
    private Project project;
    private final Boolean downloadAgain;
    private final Boolean importOnlyMasterBranch;
    private Connection connection;

    public ImportGitProjectService(Observer observer,
            Project project,
            Boolean downloadAgain,
            Boolean importOnlyMasterBranch) {
        projectDao = DaoFactory.getProjectDao();
        branchDao = DaoFactory.getBranchDao();
        commitDao = DaoFactory.getCommitDao();
        commitChangeDao = DaoFactory.getCommitChangeDao();
        this.observer = observer;
        this.project = project;
        this.importOnlyMasterBranch = importOnlyMasterBranch;
        this.downloadAgain = downloadAgain;

    }

    public void execute() throws Exception {
        try {
            Log.writeLog(project.getName(), "ImportGitProject", "Starting Import Git Project " + project.getName());
            connection = JdbcConnection.getConnection();
            Log.writeLog("Saving Project in database " + project.getName());
            project = projectDao.save(project, connection);
            Log.writeLog("Looking for branches in Project " + project.getName());
            List<Branch> branches = GitExtractor.getBranchesProject(project);
            if (branches.isEmpty()) {
                Log.writeLog("Project has no Branches " + project.getName());
                throw new ValidationException("Project has no Branches");
            }
            for (Branch branch : branches) {
                importBranch(branch);
            }
            Log.writeLog("Done.");
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            Log.writeLog("Error Exception ocurred " + e.getMessage());
            try {
                connection.rollback();
                connection.close();
            } catch (SQLException ex) {
                System.err.println("Cant rollback or close connection " + ex);
                ex.printStackTrace();
            }
            throw new Exception(e);
        }
    }

    private void importBranch(Branch branch) throws Exception {
        Log.writeLog("Saving branch" + branch.getName());
        branchDao.save(branch, connection);
        if (importOnlyMasterBranch && !branch.isBranchMaster()) {
            Log.writeLog("Branch " + branch.getName() + " will not be imported, "
                    + "because you selected to import only branch"
                    + "master and this branch is not branch master");
            return;
        }
        if (!downloadAgain && branch.isAlreadyDownloaded()) {
            Log.writeLog("Branch " + branch.getName() + " exists in local path "
                    + branch.getLocalPathDownloads() + " and you selected to not download again, "
                    + " so will not download");
        } else {
            File file = new File(branch.getLocalPathDownloads());
            if (file.exists()) {
                Log.writeLog("Deleting all files in " + branch.getLocalPathDownloads());
                FileUtils.deleteDirectory(file);
            }
            observer.sendStatusMessage("Downloading branch in " + branch.getLocalPathDownloads());
            Log.writeLog("Downloading branch in " + branch.getLocalPathDownloads());
            GitExtractor.downloadBranch(branch);
        }
        Log.writeLog("Looking for commits in Branch " + branch.getName());
        observer.sendStatusMessage("Searching commits");
        List<Commit> commits = GitExtractor.getCommits(branch);
        int totalCommits = commits.size();
        Log.writeLog("Find " + totalCommits + "commits in Branch " + branch.getName());
        Log.writeLog("Saving all commits");
        commitDao.save(commits, connection);
        int i = 0;
        Integer onePorcent = 1;
        if (totalCommits >= 100) {
            onePorcent = totalCommits / 100;
        }
        for (Commit commit : commits) {
        	Log.writeLog("Saving changes");
            commitChangeDao.save(commit.getChanges(), connection);
            i++;
            if ((i % onePorcent == 0)) {
                notifyObservers(totalCommits, i);
            }
        }
        notifyObservers(totalCommits, i);
        Log.writeLog("Finished import branch " + branch.getName());
    }

    private void notifyObservers(int totalCommits, int commitsPerformed) {
        Double d = new Double(commitsPerformed) / new Double(totalCommits);
        int progress = new Double(d * 100).intValue();
        observer.sendCurrentPercent(progress);
        observer.sendStatusMessage("Imported " + commitsPerformed + " commits of " + totalCommits);
        Log.writeLog("Imported " + commitsPerformed + " commits of " + totalCommits);
    }

}
