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
import miner.model.domain.Metric;
import miner.model.domain.Project;
import miner.model.domain.SmellEnum;
import miner.model.service.ImportCodeSmellsService;
import miner.model.service.Observer;
import miner.util.exception.ConnectionException;
import miner.util.exception.ValidationException;

/**
 *
 * @author carloseduardoxp
 */
public class ImportSmellControl {
    
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
    private List<Project> projects;
    
    private List<SmellEnum> selectedSmells;
    
    private Project selectedProject;
    
    private Boolean allCodeSmells;
   
    
    public ImportSmellControl() {
        allCodeSmells = Boolean.TRUE;
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
    
    public void importCodesmells(Observer observer) throws Exception {
        if (selectedProject == null) {
            throw new ValidationException("Select a Project");
        }
        if (!allCodeSmells && (selectedSmells == null || selectedSmells.isEmpty())) {
            throw new ValidationException("Select Some smells or switch All Code Smells");
        }
        if (allCodeSmells) {
            selectedSmells = getSmells();
        }
        ImportCodeSmellsService service = new ImportCodeSmellsService
        (observer, selectedProject,selectedSmells);
        service.execute();
    }

    public List<Project> getProjects() {
        return projects;
    }

    public List<SmellEnum> getSelectedSmells() {
        return selectedSmells;
    }

    public void setSelectedSmells(List<SmellEnum> selectedSmells) {
        this.selectedSmells = selectedSmells;
    }

    public Project getSelectedProject() {
        return selectedProject;
    }

    public void setSelectedProject(Project selectedProject) {
        this.selectedProject = selectedProject;
    }

    public Boolean getAllCodeSmells() {
        return allCodeSmells;
    }

    public void setAllCodeSmells(Boolean allCodeSmells) {
        this.allCodeSmells = allCodeSmells;
    }        

    public List<SmellEnum> getSmells() {
        return Arrays.asList(SmellEnum.values());
    }
    
    public List<Metric> getMetrics() {
        return Arrays.asList(Metric.values());
    }
       
    public void addPropertyChangeListener(PropertyChangeListener e) {
        propertyChangeSupport.addPropertyChangeListener(e);
    }
    public void removePropertyChangeListener(PropertyChangeListener e) {
        propertyChangeSupport.removePropertyChangeListener(e);
    }    

    
}
