package miner.model.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import miner.model.dao.structure.CrudDao;
import miner.model.dao.structure.DaoFactory;
import miner.model.dao.structure.ICrudDao;
import miner.model.dao.structure.xml.TypeQuery;
import miner.model.domain.Branch;
import miner.model.domain.Commit;
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
        parameters.put(2,commit.getDate());
        parameters.put(3,commit.getAuthor());
        parameters.put(4,commit.getEmailAuthor());
        parameters.put(5,commit.getSubject());
        parameters.put(6,commit.getBranch().getId());
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
            commit.setDate(rs.getDate(2));
            commit.setAuthor(rs.getString(3));
            commit.setEmailAuthor(rs.getString(4));
            commit.setSubject(rs.getString(5));
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

}
