/*
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miner.model.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import miner.model.dao.ProjectDao;
import miner.model.dao.structure.DaoFactory;
import miner.model.domain.Branch;
import miner.model.domain.Class;
import miner.model.domain.ClassCommitChange;
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

    private PrintWriter pwCsv;
    private PrintWriter pwCsvClass;
    private PrintWriter pwCommands;

    public InterRelationSmellsService(Observer observer, Project project) throws FileNotFoundException {
        projectDao = DaoFactory.getProjectDao();
        this.observer = observer;
        this.project = project;
        String fileName = project.getLocalPathWeka()+".csv";
        pwCsv = new PrintWriter(new File(fileName));
        pwCsvClass = new PrintWriter(new File(project.getLocalPathWeka()+"---Class.csv"));
        pwCommands = new PrintWriter(new File(project.getLocalPathWeka()+"Commands.csv"));
        //pwCsv.write("Smells	ClassId-ClassName\n");
        writeCommands(fileName);
    }

    private void writeCommands(String fileName) {    	
        pwCommands.write("Download R in http://cran.r-project.org \n");
        pwCommands.write("install.packages(\"arules\")\n");
        pwCommands.write("library(\"arules\")\n");
        pwCommands.write("database <- read.transactions(\""+fileName+"\",sep=\";\")\n");
        pwCommands.write("rules <- apriori(data = database, parameter = list(minlen = 2,supp=0.02,conf = 0.8))\n");
        pwCommands.write("sort_rules <- sort(rules,by=\"confidence\")\n");
        pwCommands.write("inspect(sort_rules)\n");
        pwCommands.write("install.packages(\"arulesViz\")\n");
        pwCommands.write("library(\"arulesViz\")\n");
        pwCommands.write("plot(rules,method=\"scatter\",measure=c(\"support\",\"confidence\"),shading=\"lift\")\n");
        pwCommands.write("plot(rules,method=\"grouped\",measure=c(\"support\"),shading=\"confidence\")");		
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
            pwCsv.close();
            pwCommands.close();
            pwCsvClass.close();
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
        Collections.sort(detectedSmellsList);
        if (detectedSmellsList.size() > 0) {
        	pwCsvClass.write(cast(detectedSmellsList,true)+"#"+javaClass.toString()+"\n");            
        	pwCsv.write(cast(detectedSmellsList,false)+"\n");
        }
    }

    private String cast(List<SmellEnum> detectedSmellsList,boolean increment) {
    	String value = "";
    	for (SmellEnum se: detectedSmellsList) {
    		value+= se.toString()+";";
    	}
    	value = value.substring(0, value.length()-1);
    	if (increment) {
	    	for (int i = value.length();i<90;i++) {
	    		value+=" ";
	    	}
    	}
    	return value;
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
