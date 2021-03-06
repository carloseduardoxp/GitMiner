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
import miner.model.dao.structure.JdbcConnection;
import miner.model.dao.structure.xml.TypeQuery;
import miner.model.domain.Class;
import miner.model.domain.ClassCommitChange;
import miner.model.domain.Commit;
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
        	ClassCommitChange classCommitChange = new ClassCommitChange(new Class(rs.getInt(2),rs.getString(3),rs.getBoolean(4),rs.getBoolean(5),rs.getBoolean(6)),change);
            changes.add(classCommitChange);
        }
        return changes;
    }
	
	public List<ClassCommitChange> getClassCommitsChange(Class javaClass,ICrudDao dao) throws ValidationException,SQLException,ConnectionException {
        Map<Integer, Object> parameters = new HashMap<>();
        parameters.put(1,javaClass.getId());
        ResultSet rs = dao.search("classCommitChangeByClass",parameters);        
        List<ClassCommitChange> changes = convertToClassCommitChange(rs,javaClass,dao);
        return changes;
    }
	
	private List<ClassCommitChange> convertToClassCommitChange(ResultSet rs,Class javaClass,ICrudDao dao) throws ValidationException,SQLException,ConnectionException {		
        List<ClassCommitChange> changes = new ArrayList<>();
        CommitChangeDao commitChangeDao = DaoFactory.getCommitChangeDao();
        while (rs.next()) {
        	Integer commitChangeId = rs.getInt(1);        	
        	ClassCommitChange classCommitChange = new ClassCommitChange(javaClass);
        	classCommitChange.setCommitChange(commitChangeDao.getCommitChange(commitChangeId, dao));
        	classCommitChange.setACAIC(rs.getDouble(3));
        	classCommitChange.setACMIC(rs.getDouble(4));
        	classCommitChange.setAID(rs.getDouble(5));
        	classCommitChange.setANA(rs.getDouble(6));
        	classCommitChange.setCAM(rs.getDouble(7));
        	classCommitChange.setCBO(rs.getDouble(8));
        	classCommitChange.setCIS(rs.getDouble(9));
        	classCommitChange.setCLD(rs.getDouble(10));
        	classCommitChange.setCP(rs.getDouble(11));
        	classCommitChange.setDAM(rs.getDouble(12));
        	classCommitChange.setDCAEC(rs.getDouble(13));
        	classCommitChange.setDCC(rs.getDouble(14));
        	classCommitChange.setDCMEC(rs.getDouble(15));
        	classCommitChange.setDIT(rs.getDouble(16));
        	classCommitChange.setDSC(rs.getDouble(17));
        	classCommitChange.setEIC(rs.getDouble(18));
        	classCommitChange.setEIP(rs.getDouble(19));
        	classCommitChange.setICH(rs.getDouble(20));
        	classCommitChange.setIR(rs.getDouble(21));
        	classCommitChange.setLCOM1(rs.getDouble(22));
        	classCommitChange.setLCOM2(rs.getDouble(23));
        	classCommitChange.setLCOM5(rs.getDouble(24));
        	classCommitChange.setLOC(rs.getDouble(25));
        	classCommitChange.setMcCabe(rs.getDouble(26));
        	classCommitChange.setMFA(rs.getDouble(27));
        	classCommitChange.setMOA(rs.getDouble(28));
        	classCommitChange.setNAD(rs.getDouble(29));
        	classCommitChange.setNCM(rs.getDouble(30));
        	classCommitChange.setNCP(rs.getDouble(31));
        	classCommitChange.setNMA(rs.getDouble(32));
        	classCommitChange.setNMD(rs.getDouble(33));
        	classCommitChange.setNMI(rs.getDouble(34));
        	classCommitChange.setNMO(rs.getDouble(35));
        	classCommitChange.setNOA(rs.getDouble(36));
        	classCommitChange.setNOC(rs.getDouble(37));
        	classCommitChange.setNOD(rs.getDouble(38));
        	classCommitChange.setNOF(rs.getDouble(39));
        	classCommitChange.setNOH(rs.getDouble(40));
        	classCommitChange.setNOM(rs.getDouble(41));
        	classCommitChange.setNOP(rs.getDouble(42));
        	classCommitChange.setNOPM(rs.getDouble(43));
        	classCommitChange.setNOTC(rs.getDouble(44));
        	classCommitChange.setNOTI(rs.getDouble(45));
        	classCommitChange.setNPrM(rs.getDouble(46));
        	classCommitChange.setPIIR(rs.getDouble(47));
        	classCommitChange.setPP(rs.getDouble(48));
        	classCommitChange.setREIP(rs.getDouble(49));
        	classCommitChange.setRFC(rs.getDouble(50));
        	classCommitChange.setRFP(rs.getDouble(51));
        	classCommitChange.setRPII(rs.getDouble(52));
        	classCommitChange.setRRFP(rs.getDouble(53));
        	classCommitChange.setRRTP(rs.getDouble(54));
        	classCommitChange.setRTP(rs.getDouble(55));
        	classCommitChange.setSIX(rs.getDouble(56));
        	classCommitChange.setWMC(rs.getDouble(57));
        	        				   			   			   			   
            changes.add(classCommitChange);
        }
        return changes;
    }

	public void updateMetrics(Commit commit) throws ValidationException,SQLException,ConnectionException {
		Connection connection = JdbcConnection.getConnection();
		ICrudDao dao = new CrudDao(connection);
		dao.updateBatch(TypeQuery.UPDATE,"classCommitChangeMetrics",convertListToParameters(commit));
		connection.commit();
		connection.close();
	}

	private List<Map<Integer, Object>> convertListToParameters(Commit commit) {
		List<Map<Integer, Object>> allParameters = new ArrayList<>();					
        for (CommitChange cc: commit.getChanges()) {
        	for (ClassCommitChange ccc: cc.getClassCommitchange()) {
        		allParameters.add(convertToParametersMetric(ccc));
        	}
        }
        return allParameters;
	}
	
	private Map<Integer, Object> convertToParametersMetric(ClassCommitChange classCommitChange) {
        Map<Integer,Object> parameters = new HashMap<>();
        parameters.put(1,classCommitChange.getACAIC());
        parameters.put(2,classCommitChange.getACMIC());
        parameters.put(3,classCommitChange.getAID());
        parameters.put(4,classCommitChange.getANA());
        parameters.put(5,classCommitChange.getCAM());
        parameters.put(6,classCommitChange.getCBO());
        parameters.put(7,classCommitChange.getCIS());
        parameters.put(8,classCommitChange.getCLD());
        parameters.put(9,classCommitChange.getCP());
        parameters.put(10,classCommitChange.getDAM());
        parameters.put(11,classCommitChange.getDCAEC());
        parameters.put(12,classCommitChange.getDCC());
        parameters.put(13,classCommitChange.getDCMEC());
        parameters.put(14,classCommitChange.getDIT());
        parameters.put(15,classCommitChange.getDSC());
        parameters.put(16,classCommitChange.getEIC());
        parameters.put(17,classCommitChange.getEIP());
        parameters.put(18,classCommitChange.getICH());
        parameters.put(19,classCommitChange.getIR());
        parameters.put(20,classCommitChange.getLCOM1());
        parameters.put(21,classCommitChange.getLCOM2());
        parameters.put(22,classCommitChange.getLCOM5());
        parameters.put(23,classCommitChange.getLOC());
        parameters.put(24,classCommitChange.getMcCabe());
        parameters.put(25,classCommitChange.getMFA());
        parameters.put(26,classCommitChange.getMOA());
        parameters.put(27,classCommitChange.getNAD());
        parameters.put(28,classCommitChange.getNCM());
        parameters.put(29,classCommitChange.getNCP());
        parameters.put(30,classCommitChange.getNMA());
        parameters.put(31,classCommitChange.getNMD());
        parameters.put(32,classCommitChange.getNMI());
        parameters.put(33,classCommitChange.getNMO());
        parameters.put(34,classCommitChange.getNOA());
        parameters.put(35,classCommitChange.getNOC());
        parameters.put(36,classCommitChange.getNOD());
        parameters.put(37,classCommitChange.getNOF());
        parameters.put(38,classCommitChange.getNOH());
        parameters.put(39,classCommitChange.getNOM());
        parameters.put(40,classCommitChange.getNOP());
        parameters.put(41,classCommitChange.getNOPM());
        parameters.put(42,classCommitChange.getNOTC());
        parameters.put(43,classCommitChange.getNOTI());
        parameters.put(44,classCommitChange.getNPrM());
        parameters.put(45,classCommitChange.getPIIR());
        parameters.put(46,classCommitChange.getPP());
        parameters.put(47,classCommitChange.getREIP());
        parameters.put(48,classCommitChange.getRFC());
        parameters.put(49,classCommitChange.getRFP());
        parameters.put(50,classCommitChange.getRPII());
        parameters.put(51,classCommitChange.getRRFP());
        parameters.put(52,classCommitChange.getRRTP());
        parameters.put(53,classCommitChange.getRTP());
        parameters.put(54,classCommitChange.getSIX());
        parameters.put(55,classCommitChange.getWMC());                
        
        parameters.put(56,classCommitChange.getCommitChange().getId());
        return parameters;        
    }
	
}
