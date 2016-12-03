/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miner.model.domain;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import miner.model.dao.structure.JdbcConnection;
import miner.util.exception.ValidationException;

/**
 *
 * @author carloseduardo
 */
public class Configuration {

    private String databaseName;

    private String user;

    private String password;

    private String url;

    private String pathRootSystem;

    private String pathDownloads;

    private String pathCommits;

    private String pathClasses;

    private String pathLog;

    private String pathExport;

    public Configuration(String url) {
        this.url = url;
    }

    public Configuration() {
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        if (databaseName == null) {
            databaseName = "";
        }
        this.databaseName = databaseName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        if (user == null) {
            user = "";
        }
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null) {
            password = "";
        }
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPathRootSystem() {
        return pathRootSystem;
    }

    public void setPathRootSystem(String pathRootSystem) {
        this.pathRootSystem = pathRootSystem;
    }

    public String getPathDownloads() {
        return pathDownloads;
    }

    public void setPathDownloads(String pathDownloads) {
        this.pathDownloads = pathDownloads;
    }

    public String getPathCommits() {
        return pathCommits;
    }

    public void setPathCommits(String pathCommits) {
        this.pathCommits = pathCommits;
    }

    public String getPathClasses() {
        return pathClasses;
    }

    public void setPathClasses(String pathClasses) {
        this.pathClasses = pathClasses;
    }

    public String getPathLog() {
        return pathLog;
    }

    public void setPathLog(String pathLog) {
        this.pathLog = pathLog;
    }

    public String getPathExport() {
        return pathExport;
    }

    public void setPathExport(String pathExport) {
        this.pathExport = pathExport;
    }

    public void validate(boolean newConfiguration) throws ValidationException {
        if (pathRootSystem == null || pathRootSystem.equals("")) {
            throw new ValidationException("You must enter Root System Path");
        }
        File file = new File(pathRootSystem);
        validatePath(file);
        if (!newConfiguration) {
            validatePath(new File(pathDownloads));
            validatePath(new File(pathCommits));
            validatePath(new File(pathClasses));
            validatePath(new File(pathLog));
            validatePath(new File(pathExport));
        }

        if (!JdbcConnection.testConnection(this)) {
            throw new ValidationException("Cant connect in database with parameters: " + url + " " + user + " " + password, this);
        }
    }

    public void validatePath(File f) throws ValidationException {
        if (!f.exists() || !f.isDirectory()) {
            throw new ValidationException("Directory " + f.getName() + " does not exist", this);
        }
    }

    @Override
    public Configuration clone() {
        Configuration newConfiguration = new Configuration(url);
        newConfiguration.setDatabaseName(databaseName);
        newConfiguration.setUser(user);
        newConfiguration.setPassword(password);
        newConfiguration.setPathRootSystem(pathRootSystem);
        newConfiguration.setPathClasses(pathClasses);
        newConfiguration.setPathLog(pathLog);
        newConfiguration.setPathCommits(pathCommits);
        newConfiguration.setPathDownloads(pathDownloads);
        newConfiguration.setPathExport(pathExport);
        return newConfiguration;
    }

    public void createDirectories() {
        new File(getPathClasses()).mkdirs();
        new File(getPathCommits()).mkdirs();
        new File(getPathLog()).mkdirs();
        new File(getPathExport()).mkdirs();
        new File(getPathDownloads()).mkdirs();
    }
    
    public List<String> getConfigurationFieldNames() {
       List<String> names = new ArrayList<>();
        Field[] allFields = Configuration.class.getDeclaredFields();
       for (Field field : allFields) {       
            names.add(field.getName());
        }
        return names;
    }
    
    


}
