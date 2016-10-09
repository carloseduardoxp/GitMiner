package miner.model.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry.ChangeType;

import miner.model.dao.structure.CrudDao;
import miner.model.dao.structure.DaoFactory;
import miner.model.dao.structure.ICrudDao;
import miner.model.dao.structure.xml.TypeQuery;
import miner.model.domain.Branch;
import miner.model.domain.Commit;
import miner.model.domain.CommitChange;
import miner.util.exception.ConnectionException;
import miner.util.exception.ValidationException;

public class CommitDao {

    public void save(List<Commit> commits,Connection jdbc) throws ConnectionException {
        ICrudDao dao = new CrudDao(jdbc);
        dao.updateBatch(TypeQuery.INSERT,"saveNewCommit",convertListToParameters(commits));
    }
    
    private List<Map<Integer, Object>> convertListToParameters(List<Commit> commits) {
        List<Map<Integer, Object>> allParameters = new ArrayList<>();
        commits.stream().forEach((commit) -> {
            allParameters.add(convertToParameters(commit));
        });
        return allParameters;
    }
    
    private Map<Integer, Object> convertToParameters(Commit commit) {
        Map<Integer,Object> parameters = new HashMap<>();
        		
        parameters.put(1,commit.getHash());
        parameters.put(2,commit.getAuthorName());
        parameters.put(3,commit.getAuthorEmail());
        parameters.put(4,commit.getAuthorDate());
        parameters.put(5,commit.getCommitterName());
        parameters.put(6,commit.getCommitterEmail());
        parameters.put(7,commit.getCommitterDate());
        parameters.put(8,commit.getFullMessage());
        parameters.put(9,commit.getShortMessage());
        parameters.put(10,commit.getBranch().getId());
        return parameters;        
    }    
    
    public List<Commit> getCommits(Branch branch,ICrudDao dao) throws SQLException,ConnectionException {
        Map<Integer, Object> parameters = new HashMap<>();
        parameters.put(1,branch.getId());
        ResultSet rs = dao.search("commitsWithoutChanges",parameters);
        List<Commit> commits = convertToCommit(rs,branch);
        return commits;
    }

    private List<Commit> convertToCommit(ResultSet rs,Branch branch) throws SQLException {
        List<Commit> commits = new ArrayList<>();
        while (rs.next()) {
            Commit commit = new Commit();
            commit.setHash(rs.getString(1));
            commit.setAuthorName(rs.getString(2));
            commit.setAuthorEmail(rs.getString(3));
            commit.setAuthorDate(rs.getDate(4));
            commit.setCommitterName(rs.getString(5));
            commit.setCommitterEmail(rs.getString(6));
            commit.setCommitterDate(rs.getDate(7));
            commit.setFullMessage(rs.getString(8));
            commit.setShortMessage(rs.getString(9));
            commit.setBranch(branch);
            commits.add(commit);
        }
        return commits;
    }

    public List<Commit> mountCommits(Branch branch,ICrudDao dao) throws ValidationException,SQLException,ConnectionException {
        CommitChangeDao commitChangeDao = DaoFactory.getCommitChangeDao();
        List<Commit> commits = getCommits(branch,dao);
        int i = 0;
        for (Commit commit: commits) {
        	i++;
        	if (i % 1000 == 0) {
        		dao.rebootConnection();
        	}
            commit.setChanges(commitChangeDao.mountChanges(commit,dao));
        }
        return commits;
    }
    
    public Commit getCommit(String hash,ICrudDao dao) throws ValidationException,SQLException,ConnectionException {
        Map<Integer, Object> parameters = new HashMap<>();
        parameters.put(1,hash);
        ResultSet rs = dao.search("commitByHash",parameters);
        return convertToCommit(rs);
    }
    
    private Commit convertToCommit(ResultSet rs) throws ValidationException,SQLException {
        if (rs.next()) {
            Commit commit = new Commit();
            commit.setHash(rs.getString(1));
            commit.setAuthorName(rs.getString(2));
            commit.setAuthorEmail(rs.getString(3));
            commit.setAuthorDate(rs.getDate(4));
            commit.setCommitterName(rs.getString(5));
            commit.setCommitterEmail(rs.getString(6));
            commit.setCommitterDate(rs.getDate(7));
            commit.setFullMessage(rs.getString(8));
            commit.setShortMessage(rs.getString(9));
            return commit;
        }
        throw new ValidationException("Cant find commit");
    }


}
