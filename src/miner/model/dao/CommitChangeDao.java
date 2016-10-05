/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miner.model.dao;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;

import miner.model.dao.structure.CrudDao;
import miner.model.dao.structure.DaoFactory;
import miner.model.dao.structure.ICrudDao;
import miner.model.dao.structure.xml.TypeQuery;
import miner.model.domain.Commit;
import miner.model.domain.CommitChange;
import miner.util.exception.ConnectionException;
import miner.util.exception.ValidationException;

/**
 *
 * @author carloseduardo
 */
public class CommitChangeDao {
    
    public void save(List<CommitChange> commitsChange,Connection jdbc) throws IOException,ConnectionException,
                    ValidationException{
        ICrudDao dao = new CrudDao(jdbc);
        dao.updateBatch(TypeQuery.INSERT,"saveNewCommitChange",convertListToParameters(commitsChange));       
        saveLocal(commitsChange);
    }
    
    private List<Map<Integer, Object>> convertListToParameters(List<CommitChange> commitsChange) {
        List<Map<Integer, Object>> allParameters = new ArrayList<>();
        commitsChange.stream().forEach((commitChange) -> {
            allParameters.add(convertToParameters(commitChange));
        });
        return allParameters;
    }
    
    private Map<Integer, Object> convertToParameters(CommitChange commitChange) {
        Map<Integer,Object> parameters = new HashMap<>();
        parameters.put(1,commitChange.getChangeType().toString());
        parameters.put(2,commitChange.getOldFileName());
        parameters.put(3,commitChange.getNewFileName());
        parameters.put(4,commitChange.getCommit().getHash());
        parameters.put(5,commitChange.isLocalSource());
        return parameters;        
    }       
    
    private void saveLocal(List<CommitChange> commitsChange) throws IOException,ValidationException {
        if (commitsChange.isEmpty()) {
            return;
        }        
        File dir = new File(commitsChange.get(0).getCommit().getLocalPath());
        if (!dir.exists()) {
            dir.mkdir();        
        }
        for (CommitChange commitChange: commitsChange) {
        	if (commitChange.getSourceCode() == null) {
        		if (!commitChange.isLocalSource()) {
        			continue;
        		} else {
        			throw new ValidationException("CommitChange dont have source "+commitChange);
        		}
        	}
            FileUtils.writeByteArrayToFile(new File(commitChange.getLocalPath()),commitChange.getSourceCode());
        }
    }
    
    public List<CommitChange> getCommitsChange(Commit commit,ICrudDao dao) throws ValidationException,SQLException,ConnectionException {
        Map<Integer, Object> parameters = new HashMap<>();
        parameters.put(1,commit.getHash());
        ResultSet rs = dao.search("changeCommitsByCommit",parameters);
        List<CommitChange> changes = convertToChangeCommit(rs,commit);
        return changes;
    }

    private List<CommitChange> convertToChangeCommit(ResultSet rs,Commit commit) throws ValidationException,SQLException {
        List<CommitChange> changes = new ArrayList<>();
        while (rs.next()) {
            CommitChange change = new CommitChange();
            change.setId(rs.getInt(1));
            change.setChangeType(ChangeType.valueOf(rs.getString(2)));
            change.setOldFileName(rs.getString(3));
            change.setNewFileName(rs.getString(4));
            change.setLocalSource(rs.getBoolean(5));
            change.setCommit(commit);
            changes.add(change);
        }
        return changes;
    }

    List<CommitChange> mountChanges(Commit commit,ICrudDao dao) throws SQLException,ConnectionException,ValidationException {        
        ClassCommitChangeDao changeMetricDao = DaoFactory.getClassCommitChangeDao();
        List<CommitChange> changes = getCommitsChange(commit,dao);
        for (CommitChange change: changes) {            
            change.setClassCommitchange(changeMetricDao.mountClassChanges(change,dao));
        }
        return changes;
    }
    
    public void updateClasses(Integer classId,List<Integer> changes,Connection connection) 
            throws ConnectionException,ValidationException {
        if (changes.isEmpty()) {
            throw new ValidationException("Error while updating class: No CommitChanges for classID: "+classId);        
        }
        for (Integer change: changes) {
            updateClass(classId,change,connection);
        }        
    }
    
    private Map<Integer, Object> commitsChangeUpdate(Integer classId,Integer change) {
        Map<Integer,Object> parameters = new HashMap<>();
        parameters.put(1,classId);
        parameters.put(2,change);
        return parameters;        
    }     

    public void updateClass(Integer classId, Integer change,Connection connection) throws ConnectionException,
            ValidationException{
        ICrudDao dao = new CrudDao(connection);
        Integer affectedRows = dao.update(TypeQuery.UPDATE,"updateChangeClass",
                commitsChangeUpdate(classId,change),false);       
        if (affectedRows <= 0) {
            throw new ValidationException("Error while updating class: No affectedRows CommitChangeID "+change+
                    " ClassID "+classId);
        }
    }
    
}
