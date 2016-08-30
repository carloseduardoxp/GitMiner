/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miner.model.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import miner.model.dao.structure.CrudDao;
import miner.model.dao.structure.ICrudDao;
import miner.model.dao.structure.JdbcConnection;
import miner.model.domain.Configuration;
import miner.util.exception.ConnectionException;
import miner.util.exception.ObjectNotFoundException;
import miner.util.exception.ValidationException;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author carloseduardo
 */
public class ConfigurationDao {

    private static ConfigurationDao configurationDao;

    private Configuration configuration;

    private ConfigurationDao() {

    }

    private static final String PATH_PROPERTIES_FILE = System.getProperty("user.dir")
            + "/Configuration/configuration.properties";

    private static final String PATH_CREATE_TABLE_SCRIPT = System.getProperty("user.dir")
            + "/script/dropCreateTable.sql";
    
    public static ConfigurationDao getInstance() {
        if (configurationDao == null) {
            configurationDao = new ConfigurationDao();
        }
        return configurationDao;
    }

    public Configuration getConfiguration() throws ObjectNotFoundException, ValidationException {
        if (configuration == null) {
            Properties properties = new Properties();
            try {
                InputStream file = new FileInputStream(new File(PATH_PROPERTIES_FILE));
                properties.load(file);
            } catch (IOException e) {
                throw new ObjectNotFoundException("Configuration file does not exist in "
                        + PATH_PROPERTIES_FILE + "\n" + e);
            }
            Configuration newConfiguration = new Configuration();
            List<String> fields = newConfiguration.getConfigurationFieldNames();
            Class[] paramString = new Class[1];
            paramString[0] = String.class;
            for (String fieldName : fields) {
                try {
                    String methodName = "set" + ((fieldName.charAt(0) + "").toUpperCase()) + fieldName.substring(1);
                    Method method = Configuration.class.getDeclaredMethod(methodName, paramString);
                    method.invoke(newConfiguration, properties.getProperty(fieldName));
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                    throw new ObjectNotFoundException(e.getMessage());
                }
            }
            newConfiguration.validate(false);
            configuration = newConfiguration;
        }
        return configuration;
    }

    public void save(Configuration newConfiguration) throws Exception {
        Properties properties = new Properties();
        List<String> fields = newConfiguration.getConfigurationFieldNames();
        for (String fieldName : fields) {
            String methodName = "get" + ((fieldName.charAt(0) + "").toUpperCase()) + fieldName.substring(1);
            Method method = Configuration.class
                    .getDeclaredMethod(methodName);
            Object value = method.invoke(newConfiguration);
            properties.setProperty(fieldName, value.toString());
        }

        File file = new File(PATH_PROPERTIES_FILE);
        FileUtils.deleteQuietly(file);
        try {
            file.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(file);
            properties.store(fileOut, "Configuration");
            fileOut.close();
            configuration = newConfiguration;
        } catch (IOException e) {
            throw new Exception("Cant save file " + e);
        }
    }

    public void dropCreateTables() throws ConnectionException, FileNotFoundException, SQLException {
        try (Connection jdbc = JdbcConnection.getConnection()) {
            ICrudDao dao = new CrudDao(jdbc);
            dao.executeSqlScript(new FileInputStream(new File(PATH_CREATE_TABLE_SCRIPT)));
            jdbc.commit();
            jdbc.close();
        }
    }

}
