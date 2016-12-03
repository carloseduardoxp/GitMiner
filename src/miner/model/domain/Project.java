package miner.model.domain;

import java.util.List;
import miner.model.dao.structure.DaoFactory;
import miner.util.exception.ObjectNotFoundException;
import miner.util.exception.ValidationException;

public class Project {

    private Integer id;

    private String name = "";

    private String url = "http://";    

    private List<Branch> branches;

    public Project() {

    }
    
    public Project(String name,String url) {
    	this.name = name;
    	this.url = url;
    }
    
    public Project(Integer id,String name,String url) {
    	this.id = id;
    	this.name = name;
    	this.url = url;
    }

    public void validate() throws ValidationException {
        if (name == null || name.equals("")) {
            throw new ValidationException("Enter the name of Project");
        }
        if (url == null || url.equals("")) {
            throw new ValidationException("Enter the URL Project");
        }
        if (!url.contains(".git") || !url.contains("http")) {
            throw new ValidationException("Invalid git URL");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalPathDownload() {
        try {
            return (DaoFactory.getConfigurationDao().getConfiguration().getPathDownloads()+"/" + getName()).replaceAll("\\\\","/").replaceAll("//","/");
        } catch (ObjectNotFoundException | ValidationException e) {
            throw new RuntimeException("Error on find local path " + e.getMessage());
        }     
    }
    
    public String getLocalPathClasses() {
        try {
            return (DaoFactory.getConfigurationDao().getConfiguration().getPathClasses()+"/" + getName()).replaceAll("\\\\","/").replaceAll("//","/");
        } catch (ObjectNotFoundException | ValidationException e) {
            throw new RuntimeException("Error on find local path " + e.getMessage());
        }     
    }
    
    public String getLocalPathCommits() {
        try {
            return (DaoFactory.getConfigurationDao().getConfiguration().getPathCommits()+"/" + getName()).replaceAll("\\\\","/").replaceAll("//","/");
        } catch (ObjectNotFoundException | ValidationException e) {
            throw new RuntimeException("Error on find local path " + e.getMessage());
        }     
    }
    
    public String getLocalPathWeka() {
        try {
            return (DaoFactory.getConfigurationDao().getConfiguration().getPathExport()+"/" + getName()).replaceAll("\\\\","/").replaceAll("//","/");
        } catch (ObjectNotFoundException | ValidationException e) {
            throw new RuntimeException("Error on find local path " + e.getMessage());
        }     
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Project clone() {
        Project newProject = new Project();
        newProject.setName(name);
        newProject.setUrl(url);
        newProject.setId(id);
        return newProject;
    }

}
