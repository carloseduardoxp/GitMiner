/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miner.model.dao.structure;

import miner.model.dao.BranchDao;
import miner.model.dao.ClassCommitChangeDao;
import miner.model.dao.ClassDao;
import miner.model.dao.CommitChangeDao;
import miner.model.dao.CommitDao;
import miner.model.dao.ConfigurationDao;
import miner.model.dao.DetectedSmellDao;
import miner.model.dao.ProjectDao;

/**
 *
 * @author carloseduardo
 */
public class DaoFactory {   

    private DaoFactory() {
    
    }
    
    public static ConfigurationDao getConfigurationDao() {
        return ConfigurationDao.getInstance();
    }
    
    public static ProjectDao getProjectDao() {
        return new ProjectDao();
    }
    
    public static BranchDao getBranchDao() {
        return new BranchDao();
    }

    public static CommitDao getCommitDao() {
        return new CommitDao();
    }
    
    public static CommitChangeDao getCommitChangeDao() {
        return new CommitChangeDao();
    }
    
    public static ClassDao getClassDao() {
        return new ClassDao();
    }
    
    public static DetectedSmellDao getDetectedSmellDao() {
        return new DetectedSmellDao();
    }
    
    public static ClassCommitChangeDao getClassCommitChangeDao() {
        return new ClassCommitChangeDao();
    }
    
    
}
