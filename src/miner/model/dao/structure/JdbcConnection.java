package miner.model.dao.structure;

import java.sql.Connection;
import java.sql.SQLException;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import java.sql.DriverManager;
import miner.model.domain.Configuration;
import miner.util.exception.ConnectionException;
import miner.util.exception.ObjectNotFoundException;
import miner.util.exception.ValidationException;

public class JdbcConnection {

    private static MysqlConnectionPoolDataSource cpds;

    public static Connection getConnection() throws ConnectionException {
        try {
            Connection c = getPool().getPooledConnection().getConnection();
            c.setAutoCommit(false);
            return c;
        } catch (ObjectNotFoundException | ValidationException e) {
            throw new ConnectionException("Error: cant connect in database, problem in configuration file:" + e, "miner.model.dao.ConexaoJdbc");
        } catch (SQLException e) {
            throw new ConnectionException("Error: cant connect in database:" + e.getSQLState() + " - " + e, "miner.model.dao.ConexaoJdbc");
        }

    }

    private static MysqlConnectionPoolDataSource getPool() throws ObjectNotFoundException, ValidationException {
        if (cpds == null) {
            Configuration configuration = DaoFactory.getConfigurationDao().getConfiguration();
            cpds = new MysqlConnectionPoolDataSource();
            cpds.setUser(configuration.getUser());
            cpds.setPassword(configuration.getPassword());
            cpds.setURL(configuration.getUrl());
            cpds.setCachePreparedStatements(true);
            cpds.setAutoReconnect(true);
        }
        return cpds;
    }

    public static boolean testConnection(Configuration configuration) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection c = DriverManager.getConnection(configuration.getUrl(), configuration.getUser(),
                    configuration.getPassword());
            c.close();
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

}
