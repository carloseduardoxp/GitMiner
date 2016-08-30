/*
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miner.model.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import miner.model.dao.ProjectDao;
import miner.model.dao.structure.DaoFactory;
import miner.model.domain.Branch;
import miner.model.domain.Class;
import miner.model.domain.ClassCommitChange;
import miner.model.domain.CommitChange;
import miner.model.domain.DetectedSmell;
import miner.model.domain.Project;
import miner.model.domain.SmellEnum;
import miner.util.Log;

/**
 *
 * @author carloseduardoxp
 */
public class InterRelationSmellsService {

    private Project project;

    private Observer observer;

    private ProjectDao projectDao;

    private PrintWriter pw;

    public InterRelationSmellsService(Observer observer, Project project) throws FileNotFoundException {
        projectDao = DaoFactory.getProjectDao();
        this.observer = observer;
        this.project = project;
        pw = new PrintWriter(new File(project.getLocalPathWeka()+ "InterRelationSmells.arff"));
        pw.write("@RELATION " + project.getName() + "\n\n");
        pw.write(SmellEnum.getNomeSmells() + "\n\n");
        pw.write("@DATA\n");

    }

    public void execute() throws Exception {
        try {
            Log.writeLog(project.getName(), "InterRelationSmells", "Starting Generating Report Inter Relation Smells Service " + project.getName());
            Log.writeLog("Mounting Project (branches, commits and commit changes) " + project.getName());
            observer.sendStatusMessage("Mounting Project (branches, commits and commit changes) " + project.getName());
            project = projectDao.mountProject(project);
            for (Branch branch : project.getBranches()) {
                analyseBranch(branch);
            }
            pw.close();
            Log.writeLog("Done.");            
        } catch (Exception e) {
            e.printStackTrace();
            Log.writeLog("Error Exception ocurred " + e.getMessage());
            throw new Exception(e);
        }                
    }

    private void analyseBranch(Branch branch) {
        Log.writeLog("Analysing branch " + branch.getName());
        List<Class> classes = branch.getClasses();
        int totalClasses = classes.size();
        int i = 0;
        Integer onePorcent = 1;
        if (totalClasses >= 100) {
            onePorcent = totalClasses / 100;
        }
        for (Class javaClass : classes) {
            analyseClass(javaClass);
            i++;
            if ((i % onePorcent == 0)) {
                notifyObservers(totalClasses, i);
            }
        }
        notifyObservers(totalClasses, i);
    }

    private void analyseClass(Class javaClass) {
        Log.writeLog("Analysing class " + javaClass.getName());
        Set<SmellEnum> detectedSmells = new LinkedHashSet<>();
        List<ClassCommitChange> arquivosCommit = javaClass.getChanges();
        for (ClassCommitChange arquivoCommit : arquivosCommit) {
            detectedSmells.addAll(analyseChange(arquivoCommit));
        }
        javaClass.setSmells(detectedSmells);
        List<SmellEnum> detectedSmellsList = new ArrayList<>(detectedSmells);
        if (detectedSmellsList.size() > 0) {
            for (int j = 0; j < SmellEnum.values().length; j++) {
                SmellEnum smell = SmellEnum.values()[j];
                if (detectedSmellsList.contains(smell)) {
                    pw.write("y");
                } else {
                    pw.write("?");
                }
                if (j == SmellEnum.values().length - 1) {
                    pw.write("\n");
                } else {
                    pw.write(",");
                }
            }
        }
    }

    private Set<SmellEnum> analyseChange(ClassCommitChange change) {
        List<DetectedSmell> smells = change.getSmells();
        Set<SmellEnum> detectedSmells = new LinkedHashSet<>();
        for (DetectedSmell smell : smells) {
            detectedSmells.add(smell.getSmell());
        }
        return detectedSmells;
    }
    
    private void notifyObservers(int totalCommits, int commitsPerformed) {
        Double d = new Double(commitsPerformed) / new Double(totalCommits);
        int progress = new Double(d * 100).intValue();
        observer.sendCurrentPercent(progress);
        observer.sendStatusMessage("Analysed " + commitsPerformed + " commits of " + totalCommits);
        Log.writeLog("Analysed " + commitsPerformed + " commits of " + totalCommits);
    }

}
