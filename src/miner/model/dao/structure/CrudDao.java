package miner.model.dao.structure;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import miner.model.dao.structure.xml.DatabaseOperation;
import miner.model.dao.structure.xml.TypeQuery;
import miner.util.exception.ConnectionException;

public class CrudDao implements ICrudDao {

    private Connection connection;

    public CrudDao(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public ResultSet search(String query) throws ConnectionException {
        return search(query,new HashMap<>());
    }

    @Override
    public ResultSet search(String query, Map<Integer, Object> parameters) throws ConnectionException {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(DatabaseOperation.getSql(TypeQuery.SELECT, query));
            statement = setParameters(statement, parameters);
            return statement.executeQuery();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (Exception e2) {
                System.out.println("error in execute rollback method " + e2);
            }
            throw new ConnectionException("Error in execute method search with parameters "
                    + query + " " + parameters + " " + e.getSQLState() + e.getMessage(), this.getClass().getName());
        } 
    }

    @Override
    public Integer update(TypeQuery type, String query, Map<Integer, Object> parameters, boolean autoIncrement)
            throws ConnectionException {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(DatabaseOperation.getSql(type, query),
                    Statement.RETURN_GENERATED_KEYS);
            statement = setParameters(statement, parameters);
            int affectedRows = statement.executeUpdate();
            if (autoIncrement) {                
                ResultSet rs = statement.getGeneratedKeys();
                rs.next();
                int newId = rs.getInt(1);
                rs.close();
                return newId;
            } else {
                return affectedRows;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (Exception e2) {
                System.out.println("error in execute rollback method " + e2);
            }
            throw new ConnectionException("Error in execute method update with parameters " + type + " "
                    + query + " " + parameters + " " + e.getSQLState() + e.getMessage(), this.getClass().getName());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }               
            } catch (Exception e2) {
                System.err.println("Error in close resultset " + e2);
            }
        }
    }

    @Override
    public void executeSqlScript(InputStream in) throws SQLException {
        Scanner s = new Scanner(in);
        s.useDelimiter("/\\*[\\s\\S]*?\\*/|--[^\\r\\n]*|;");

        Statement st = null;

        try {
            st = connection.createStatement();

            while (s.hasNext()) {
                String line = s.next().trim();

                if (!line.isEmpty()) {
                    st.execute(line);
                }
            }
        } finally {
            if (st != null) {
                st.close();
            }
        }
    }

    private PreparedStatement setParameters(PreparedStatement statement, Map<Integer, Object> parameters) 
            throws SQLException {
        for (Integer key : parameters.keySet()) {
            statement.setObject(key, parameters.get(key));
        }
        return statement;
    }

    @Override
    public void updateBatch(TypeQuery type, String query, List<Map<Integer, Object>> allParameters) throws ConnectionException {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(DatabaseOperation.getSql(type, query));
            for (Map<Integer, Object> parameters: allParameters) {
                 statement = setParameters(statement, parameters);   
                 statement.addBatch();
            }            
            statement.executeBatch();            
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (Exception e2) {
                System.out.println("error in execute rollback method " + e2);
            }
            throw new ConnectionException("Error in execute method update with parameters " + type + " "
                    + query + " " + allParameters + " " + e.getSQLState() + e.getMessage(), this.getClass().getName());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }               
            } catch (Exception e2) {
                System.err.println("Error in close resultset " + e2);
            }
        }
    }

	@Override
	public void rebootConnection() throws SQLException,ConnectionException {
		connection.close();
		connection = JdbcConnection.getConnection();		
	}

}
