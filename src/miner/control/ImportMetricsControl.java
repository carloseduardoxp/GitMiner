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
import java.util.List;

import org.jdesktop.observablecollections.ObservableCollections;

import miner.model.dao.ProjectDao;
import miner.model.dao.structure.DaoFactory;
import miner.model.domain.Project;
import miner.model.service.ImportMetricsService;
import miner.model.service.Observer;
import miner.util.exception.ConnectionException;
import miner.util.exception.ValidationException;

/**
 *
 * @author carloseduardoxp
 */
public class ImportMetricsControl {
    
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);   
    
    private List<Project> projects;    
    
    private Project selectedProject;
    
    public ImportMetricsControl() {
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
    
    public void importMetrics(Observer observer) throws Exception {
        if (selectedProject == null) {
            throw new ValidationException("Select a Project");
        }
        ImportMetricsService service = new ImportMetricsService(observer, selectedProject);
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
    
    public void addPropertyChangeListener(PropertyChangeListener e) {
        propertyChangeSupport.addPropertyChangeListener(e);
    }
    public void removePropertyChangeListener(PropertyChangeListener e) {
        propertyChangeSupport.removePropertyChangeListener(e);
    }    

    
}
