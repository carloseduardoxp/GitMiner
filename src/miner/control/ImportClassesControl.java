/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miner.control;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jdesktop.observablecollections.ObservableCollections;

import miner.model.dao.ProjectDao;
import miner.model.dao.structure.DaoFactory;
import miner.model.domain.Project;
import miner.model.service.ImportClassesService;
import miner.model.service.Observer;
import miner.util.exception.ConnectionException;
import miner.util.exception.ValidationException;

/**
 *
 * @author carloseduardoxp
 */
public class ImportClassesControl {
    
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);   
    
    private List<Project> projects;    
    
    private Project selectedProject;
    
    private String filters;
    
    public ImportClassesControl() {
    	setFilters("/test/;/examples/;/example/");
        ProjectDao projectDao = DaoFactory.getProjectDao();
        projects = ObservableCollections.observableList(new ArrayList<Project>());
        projects.clear();  
        try {
            projects.addAll(projectDao.getProjectsWithoutBranches());
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ConnectionException ex) {
            ex.printStackTrace();
        }
    }   
    
    public void importClasses(Observer observer) throws Exception {
        if (selectedProject == null) {
            throw new ValidationException("Select a Project");
        }
        String[] filtersArray = filters.split(";");
        ImportClassesService service = new ImportClassesService(observer, selectedProject,Arrays.asList(filtersArray));
        service.execute();
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public Project getSelectedProject() {
        return selectedProject;
    }

    public void setSelectedProject(Project selectedProject) {
        this.selectedProject = selectedProject;
    }            
    
    public String getFilters() {
		return filters;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}

	public void addPropertyChangeListener(PropertyChangeListener e) {
        propertyChangeSupport.addPropertyChangeListener(e);
    }
    public void removePropertyChangeListener(PropertyChangeListener e) {
        propertyChangeSupport.removePropertyChangeListener(e);
    }    

    
}
