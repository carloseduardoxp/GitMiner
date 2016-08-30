package miner.model.dao;

import java.io.IOException;
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
import miner.model.domain.Class;
import miner.model.domain.ClassCommitChange;
import miner.model.domain.CommitChange;
import miner.util.exception.ConnectionException;
import miner.util.exception.ValidationException;

public class ClassCommitChangeDao {
	
	public void save(List<ClassCommitChange> classCommitsChange,Connection jdbc) throws IOException,ConnectionException {
        ICrudDao dao = new CrudDao(jdbc);
        dao.updateBatch(TypeQuery.INSERT,"saveNewClassCommitChange",convertListToParameters(classCommitsChange));       
    }
	
	private List<Map<Integer, Object>> convertListToParameters(List<ClassCommitChange> commitsChange) {
        List<Map<Integer, Object>> allParameters = new ArrayList<>();
        commitsChange.stream().forEach((commitChange) -> {
            allParameters.add(convertToParameters(commitChange));
        });
        return allParameters;
    }
	
	private Map<Integer, Object> convertToParameters(ClassCommitChange classCommitChange) {
        Map<Integer,Object> parameters = new HashMap<>();
        parameters.put(1,classCommitChange.getJavaClass().getId());
        parameters.put(2,classCommitChange.getCommitChange().getId());
        return parameters;        
    }

	public List<ClassCommitChange> mountClassChanges(CommitChange change, ICrudDao dao) throws SQLException,ConnectionException,ValidationException {        
		DetectedSmellDao detectedSmellDao = DaoFactory.getDetectedSmellDao();
		List<ClassCommitChange> changes = getClassCommitsChange(change,dao);
		for (ClassCommitChange classCommit: changes) {
			classCommit.setSmells(detectedSmellDao.mountDetectedSmells(classCommit,dao));	
		}	
		return changes;
	}

	public List<ClassCommitChange> mountClassChanges(Class javaClass, ICrudDao dao) throws SQLException,ConnectionException,ValidationException {        
		DetectedSmellDao detectedSmellDao = DaoFactory.getDetectedSmellDao();
		List<ClassCommitChange> changes = getClassCommitsChange(javaClass,dao);
		for (ClassCommitChange classCommit: changes) {
			classCommit.setSmells(detectedSmellDao.mountDetectedSmells(classCommit,dao));	
		}	
		return changes;
	}       
	
	public List<ClassCommitChange> getClassCommitsChange(CommitChange change,ICrudDao dao) throws ValidationException,SQLException,ConnectionException {
        Map<Integer, Object> parameters = new HashMap<>();
        parameters.put(1,change.getId());
        ResultSet rs = dao.search("classCommitChangeByCommit",parameters);
        List<ClassCommitChange> changes = convertToClassCommitChange(rs,change);
        return changes;
    }
	
	private List<ClassCommitChange> convertToClassCommitChange(ResultSet rs,CommitChange change) throws ValidationException,SQLException {		
        List<ClassCommitChange> changes = new ArrayList<>();
        while (rs.next()) {
        	ClassCommitChange classCommitChange = new ClassCommitChange(new Class(rs.getInt(2)),change);
            changes.add(classCommitChange);
        }
        return changes;
    }
	
	public List<ClassCommitChange> getClassCommitsChange(Class javaClass,ICrudDao dao) throws ValidationException,SQLException,ConnectionException {
        Map<Integer, Object> parameters = new HashMap<>();
        parameters.put(1,javaClass.getId());
        ResultSet rs = dao.search("classCommitChangeByClass",parameters);
        List<ClassCommitChange> changes = convertToClassCommitChange(rs,javaClass);
        return changes;
    }
	
	private List<ClassCommitChange> convertToClassCommitChange(ResultSet rs,Class javaClass) throws ValidationException,SQLException {		
        List<ClassCommitChange> changes = new ArrayList<>();
        while (rs.next()) {
        	ClassCommitChange classCommitChange = new ClassCommitChange(javaClass,new CommitChange(rs.getInt(2)));
            changes.add(classCommitChange);
        }
        return changes;
    }
	
}
