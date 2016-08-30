package miner.model.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import miner.model.dao.structure.ICrudDao;
import miner.model.domain.ClassCommitChange;
import miner.model.domain.CommitChange;
import miner.model.domain.DetectedSmell;
import miner.model.domain.SmellEnum;
import miner.util.exception.ConnectionException;
import miner.util.exception.ValidationException;

public class DetectedSmellDao {

    public List<DetectedSmell> getDetectedSmells(ClassCommitChange change,ICrudDao dao) throws ValidationException,SQLException,ConnectionException {
        Map<Integer, Object> parameters = new HashMap<>();
        parameters.put(1,change.getCommitChange().getId());
        parameters.put(2,change.getJavaClass().getId());        
        ResultSet rs = dao.search("smellsByChange",parameters);
        List<DetectedSmell> commits = convertToDetectedSmells(rs,change);
        return commits;
    }

    private List<DetectedSmell> convertToDetectedSmells(ResultSet rs,ClassCommitChange change) throws ValidationException,SQLException {
        List<DetectedSmell> detectedSmells = new ArrayList<>();
        while (rs.next()) {
            DetectedSmell detectedSmell = new DetectedSmell();
            detectedSmell.setId(rs.getInt(1));
            detectedSmell.setSmell(SmellEnum.getSmellName(rs.getString(2)));
            detectedSmell.setObs(rs.getString(3));
            detectedSmell.setChange(change);
            detectedSmells.add(detectedSmell);
        }
        return detectedSmells;
    }

    public List<DetectedSmell> mountDetectedSmells(ClassCommitChange change,ICrudDao dao) throws ValidationException,SQLException,ConnectionException {
        return getDetectedSmells(change,dao);
    }    

    public void insertDetectedSmells(Map<CommitChange, Set<SmellEnum>> detectedSmells, Connection connection) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
