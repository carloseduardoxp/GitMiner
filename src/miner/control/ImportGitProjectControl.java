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
import miner.model.dao.ProjectDao;
import miner.model.dao.structure.DaoFactory;
import miner.model.domain.Project;
import miner.model.service.ImportGitProjectService;
import miner.util.exception.ConnectionException;
import org.jdesktop.observablecollections.ObservableCollections;
import miner.model.service.Observer;

/**
 *
 * @author carloseduardoxp
 */
public final class ImportGitProjectControl {

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private Project project;

    private List<Project> projects;

    private Boolean downloadAgain;

    private Boolean importOnlyMasterBranch;

    private final ProjectDao projectDao;

    public ImportGitProjectControl() {
        this.projectDao = DaoFactory.getProjectDao();
        downloadAgain = Boolean.FALSE;
        importOnlyMasterBranch = Boolean.TRUE;
        projects = ObservableCollections.observableList(new ArrayList<Project>());
        this.newProject();
        this.searchProjects();
        this.updateLocalPath();
    }

    public void newProject() {
        setProject(new Project());
    }

    public void updateLocalPath() {
        Project newProject = project.clone();
        setProject(newProject);
    }

    public void searchProjects() {
        projects.clear();
        try {
            projects.addAll(projectDao.getProjectsWithoutBranches());
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ConnectionException ex) {
            ex.printStackTrace();
        }
    }

    public void importProject(Observer observer) throws Exception {
        this.project.validate();
        ImportGitProjectService service = new ImportGitProjectService(observer, project,
                downloadAgain, importOnlyMasterBranch);
        service.execute();
        newProject();
        searchProjects();
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        Project oldProject = this.project;
        this.project = project;
        propertyChangeSupport.firePropertyChange("project", oldProject, project);
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }  

    public Boolean getDownloadAgain() {
        return downloadAgain;
    }

    public void setDownloadAgain(Boolean downloadAgain) {
        this.downloadAgain = downloadAgain;
    }

    public Boolean getImportOnlyMasterBranch() {
        return importOnlyMasterBranch;
    }

    public void setImportOnlyMasterBranch(Boolean importOnlyMasterBranch) {
        this.importOnlyMasterBranch = importOnlyMasterBranch;
    }

    public void addPropertyChangeListener(PropertyChangeListener e) {
        propertyChangeSupport.addPropertyChangeListener(e);
    }

    public void removePropertyChangeListener(PropertyChangeListener e) {
        propertyChangeSupport.removePropertyChangeListener(e);
    }

}
