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
import miner.model.dao.structure.JdbcConnection;
import miner.model.dao.structure.xml.TypeQuery;
import miner.model.domain.Branch;
import miner.model.domain.Class;
import miner.model.domain.ClassCommitChange;
import miner.model.domain.CommitChange;
import miner.util.Log;
import miner.util.exception.ConnectionException;
import miner.util.exception.InvalidCodeException;
import miner.util.exception.ValidationException;

public class ClassDao {

    public List<Class> getClasses(Branch branch, ICrudDao dao) throws SQLException, ConnectionException {
        Map<Integer, Object> parameters = new HashMap<>();
        parameters.put(1, branch.getId());
        ResultSet rs = dao.search("classesWithoutChanges", parameters);
        List<Class> classes = convertToClass(rs, branch);
        return classes;
    }

    private List<Class> convertToClass(ResultSet rs, Branch branch) throws SQLException {
        List<Class> classes = new ArrayList<>();
        while (rs.next()) {
            Class javaClass = new Class();
            javaClass.setId(rs.getInt(1));
            javaClass.setName(rs.getString(2));
            javaClass.setBranch(branch);
            classes.add(javaClass);
        }
        return classes;
    }

    List<Class> mountClasses(Branch branch, ICrudDao dao) throws ValidationException, SQLException, ConnectionException {
        ClassCommitChangeDao commitChangeDao = DaoFactory.getClassCommitChangeDao();
        List<Class> classes = getClasses(branch, dao);
        for (Class javaClass : classes) {
            javaClass.setChanges(commitChangeDao.mountClassChanges(javaClass, dao));
        }
        return classes;
    }

    public void insertClasses(Branch branch, Map<Class, List<CommitChange>> classes,Connection connection) 
    		throws ConnectionException, ValidationException, SQLException {
        ICrudDao dao = new CrudDao(connection);
        ClassCommitChangeDao changeDao = DaoFactory.getClassCommitChangeDao();
        for (Class key : classes.keySet()) {
            Class javaClass = save(key, dao);
            List<ClassCommitChange> classCommits = new ArrayList<>();
            List<CommitChange> changes = classes.get(key);
            for (CommitChange change : changes) {            	
            	classCommits.add(new ClassCommitChange(javaClass, change));
            }
            try {                	             
              	changeDao.save(classCommits, connection);            
            } catch (Exception e) {
                e.printStackTrace();
                Log.writeLog(e.getMessage());                 
            }
        }        
    }

    private Class save(Class javaClass, ICrudDao dao) throws ConnectionException {
        Integer classId = dao.update(TypeQuery.INSERT, "saveNewClass", convertToParameters(javaClass), true);
        javaClass.setId(classId);
        return javaClass;
    }

    private Map<Integer, Object> convertToParameters(Class javaClass) {
        Map<Integer, Object> parameters = new HashMap<>();
        parameters.put(1, javaClass.getName());
        parameters.put(2, javaClass.getBranch().getId());
        return parameters;
    }

    private Integer getClassId(CommitChange change, ICrudDao dao) throws InvalidCodeException,
            ConnectionException, SQLException, ValidationException {
        Map<Integer, Object> parameters = new HashMap<>();
        parameters.put(1, change.getFileName());
        ResultSet rs = dao.search("classByChange", parameters);
        Integer classId = null;
        while (rs.next()) {
            if (classId == null) {
                classId = rs.getInt(1);
            } else {
                Log.writeLog("WARNING: update classes without code: Filename " + change
                        + " has more than one CLASS: " + classId + " and " + rs.getInt(1));
                parameters.put(2, change.getId());
                rs = dao.search("classByChangeDuplicate", parameters);
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new ValidationException("Error in update classes without code: Filename " + change
                            + " started with duplicate classes, and now has no CLASS");
                }
            }
        }
        if (classId == null) {
            throw new InvalidCodeException("WARNING: update classes without code: Filename " + change
                    + " has no CLASS, but cant found source code in this class");
        }
        rs.close();
        return classId;
    }

}
