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
import miner.model.dao.structure.xml.TypeQuery;
import miner.model.domain.Branch;
import miner.model.domain.Project;
import miner.util.exception.ConnectionException;
import miner.util.exception.ValidationException;

public class BranchDao {

    public Branch save(Branch branch,Connection jdbc) throws ConnectionException {
        ICrudDao dao = new CrudDao(jdbc);
        Integer branchId = dao.update(TypeQuery.INSERT,"saveNewBranch",convertToParameters(branch),true);
        branch.setId(branchId);
        return branch;
    }
    
    private Map<Integer, Object> convertToParameters(Branch branch) {
        Map<Integer,Object> parameters = new HashMap<>();
        parameters.put(1,branch.getName());
        parameters.put(2,branch.getProject().getId());
        return parameters;        
    }

    public List<Branch> getBranches(Project project,ICrudDao dao) throws SQLException,ConnectionException {        
        Map<Integer, Object> parameters = new HashMap<>();
        parameters.put(1,project.getId());
        ResultSet rs = dao.search("branchesWithoutClassesCommits",parameters);
        List<Branch> branches = convertToBranch(rs,project);
        return branches;
    }

    private List<Branch> convertToBranch(ResultSet rs,Project project) throws SQLException {
        List<Branch> branches = new ArrayList<>();
        while (rs.next()) {
            Branch branch = new Branch();
            branch.setId(rs.getInt(1));
            branch.setName(rs.getString(2));
            branch.setProject(project);
            branches.add(branch);
        }
        return branches;
    }

    public List<Branch> mountBranches(Project project,ICrudDao dao) throws ValidationException,SQLException,ConnectionException {
        ClassDao classDao = DaoFactory.getClassDao();
        CommitDao commitDao = DaoFactory.getCommitDao();
        List<Branch> branches = getBranches(project,dao);
        for (Branch branch: branches) {
            branch.setCommits(commitDao.mountCommits(branch,dao));
            branch.setClasses(classDao.mountClasses(branch,dao));
        }
        return branches;
    }

}
