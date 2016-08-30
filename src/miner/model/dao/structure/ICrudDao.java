/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miner.model.dao.structure;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import miner.model.dao.structure.xml.TypeQuery;
import miner.util.exception.ConnectionException;

/**
 *
 * @author carloseduardo
 */
public interface ICrudDao {
    
    ResultSet search(String query) throws ConnectionException;

    ResultSet search(String query, Map<Integer, Object> parameters) throws ConnectionException;

    Integer update(TypeQuery type, String query, Map<Integer, Object> parameters, boolean autoIncrement) throws ConnectionException;    
    
    void updateBatch(TypeQuery type, String query, List<Map<Integer, Object>> parameters) throws ConnectionException;    
    
    void executeSqlScript(InputStream in) throws SQLException;
}
