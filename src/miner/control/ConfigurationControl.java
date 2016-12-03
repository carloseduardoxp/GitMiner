/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miner.control;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import miner.model.dao.ConfigurationDao;
import miner.model.dao.structure.DaoFactory;
import miner.model.domain.Configuration;
import miner.util.exception.ObjectNotFoundException;
import miner.util.exception.ValidationException;

/**
 *
 * @author carloseduardo
 * 
 */
public final class ConfigurationControl {
    
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/:databaseName?min-pool-size=5&max-pool-size=100";           
    
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);   
    
    private Configuration configuration;
    
    private ConfigurationDao configurationDao;
    
    private boolean dropCreateTable;
    
    public ConfigurationControl() {
        setDropCreateTable(true);
        configurationDao = DaoFactory.getConfigurationDao();
        try {
            setConfiguration(configurationDao.getConfiguration());
        } catch(ObjectNotFoundException e) {
            setConfiguration(new Configuration(DEFAULT_URL));
        } catch(ValidationException e) {
            setConfiguration((Configuration)e.getO());
        }           
    }

    public void salvar() throws ValidationException,Exception {
        configuration.validate(true);        
        configurationDao.save(configuration);
        configuration.createDirectories();
        if (dropCreateTable) {
            configurationDao.dropCreateTables();
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        Configuration oldConfiguration = this.configuration;
        this.configuration = configuration;
        propertyChangeSupport.firePropertyChange("configuration", oldConfiguration, configuration);
    }    
    
    public void addPropertyChangeListener(PropertyChangeListener e) {
        propertyChangeSupport.addPropertyChangeListener(e);
    }
    public void removePropertyChangeListener(PropertyChangeListener e) {
        propertyChangeSupport.removePropertyChangeListener(e);
    }   

    public void updateUrl() {
       Configuration newConfiguration = configuration.clone();
       if (configuration.getDatabaseName() != null && !configuration.getDatabaseName().equals("")) {
           String newUrl = newConfiguration.getUrl().replace(":databaseName", configuration.getDatabaseName());
           newConfiguration.setUrl(newUrl);
       }       
       setConfiguration(newConfiguration);
    }

    public void updateDirs() {
       Configuration newConfiguration = configuration.clone();
       newConfiguration.setPathCommits(configuration.getPathRootSystem()+"/commits/");
       newConfiguration.setPathClasses(configuration.getPathRootSystem()+"/classes/");
       newConfiguration.setPathLog(configuration.getPathRootSystem()+"/log/");
       newConfiguration.setPathDownloads(configuration.getPathRootSystem()+"/downloads/");
       newConfiguration.setPathExport(configuration.getPathRootSystem()+"/export/");   
       setConfiguration(newConfiguration);
    }

    public boolean isDropCreateTable() {
        return dropCreateTable;
    }

    public void setDropCreateTable(boolean dropCreateTable) {
        this.dropCreateTable = dropCreateTable;
    }    
    
}
