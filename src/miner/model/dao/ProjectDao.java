package miner.model.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miner.model.dao.structure.CrudDao;
import miner.model.dao.structure.DaoFactory;
import miner.model.dao.structure.ICrudDao;
import miner.model.dao.structure.JdbcConnection;
import miner.model.dao.structure.xml.TypeQuery;

import miner.model.domain.Project;
import miner.util.exception.ConnectionException;
import miner.util.exception.ValidationException;

public class ProjectDao {

    public Project save(Project project,Connection jdbc) throws ConnectionException {
        ICrudDao dao = new CrudDao(jdbc);
        Integer projectId = dao.update(TypeQuery.INSERT,"saveNewProject",convertToParameters(project),true);
        project.setId(projectId);
        return project;
    }

    public List<Project> getProjectsWithoutBranches() throws SQLException,ConnectionException{
        Connection jdbc = JdbcConnection.getConnection();
        ICrudDao dao = new CrudDao(jdbc);
        ResultSet rs = dao.search("projectsWithoutBranches");
        List<Project> projects = convertToProject(rs);
        jdbc.close();
        return projects;
    }

    private List<Project> convertToProject(ResultSet rs) throws SQLException {
        List<Project> projects = new ArrayList<>();
        while (rs.next()) {
            Project project = new Project();
            project.setId(rs.getInt(1));
            project.setName(rs.getString(2));
            project.setUrl(rs.getString(3));
            projects.add(project);
        }
        return projects;
    }

    private Map<Integer, Object> convertToParameters(Project project) {
        Map<Integer,Object> parameters = new HashMap<>();
        parameters.put(1,project.getName());
        parameters.put(2,project.getUrl());
        return parameters;        
    }

    public Project mountProject(Project project) throws ValidationException,SQLException,ConnectionException {        
        Connection connection = JdbcConnection.getConnection();
        ICrudDao dao = new CrudDao(connection);
        BranchDao branchDao = DaoFactory.getBranchDao();
        project.setBranches(branchDao.mountBranches(project,dao));  
        connection.close();
        return project;
    }

}
