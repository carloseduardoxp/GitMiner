package miner.model.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import miner.model.dao.DetectedSmellDao;
import miner.model.dao.ProjectDao;
import miner.model.dao.structure.DaoFactory;
import miner.model.dao.structure.JdbcConnection;
import miner.model.domain.Branch;
import miner.model.domain.ClassCommitChange;
import miner.model.domain.Commit;
import miner.model.domain.CommitChange;
import miner.model.domain.DetectedSmell;
import miner.model.domain.Project;
import miner.model.domain.SmellEnum;
import miner.util.Log;
import padl.analysis.repository.AACRelationshipsAnalysis;
import padl.creator.javafile.eclipse.CompleteJavaFileCreator;
import padl.kernel.ICodeLevelModel;
import padl.kernel.IIdiomLevelModel;
import padl.kernel.impl.Factory;
import sad.designsmell.detection.IDesignSmellDetection;
import sad.kernel.ICodeSmell;
import sad.kernel.impl.CodeSmell;
import sad.kernel.impl.CodeSmellComposite;
import sad.kernel.impl.DesignSmell;

public class ImportCodeSmellsService {

    private static DetectedSmellDao detectedSmellDao;
    private final ProjectDao projectDao;
    private final Observer observer;
    private Project project;
    private final List<SmellEnum> smells;
    private Map<CommitChange, Set<SmellEnum>> detectedSmells;
    private Connection connection;

    public ImportCodeSmellsService(Observer observer, Project project, List<SmellEnum> smells) {
        detectedSmellDao = DaoFactory.getDetectedSmellDao();
        projectDao = DaoFactory.getProjectDao();
        this.observer = observer;
        this.project = project;
        this.smells = smells;
    }

    public void execute() throws Exception {
        try {
            Log.writeLog(project.getName(), "ImportSmells", "Starting Import Smells of Project " + project.getName());
            connection = JdbcConnection.getConnection();
            Log.writeLog("Mounting Project (branches, commits and commit changes) " + project.getName());
            observer.sendStatusMessage("Mounting Project (branches, commits and commit changes) " + project.getName());
            project = projectDao.mountProject(project);
            for (Branch branch : project.getBranches()) {
                analyseBranch(branch);
            }
            Log.writeLog("Done.");
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            Log.writeLog("Error Exception ocurred " + e.getMessage());
            try {
                connection.rollback();
                connection.close();
            } catch (SQLException ex) {
                System.err.println("Cant rollback or close connection " + ex);
                ex.printStackTrace();
            }
            throw new Exception(e);
        }
    }

    private void analyseBranch(Branch branch) throws Exception {
        Log.writeLog("Analysing branch " + branch.getName());
        this.detectedSmells = new HashMap<>();
        List<Commit> commits = branch.getCommits();
        if (commits.isEmpty()) {
        	Log.writeLog("Branch "+branch.getName()+" dont have commits");
        	return;
        }
        int totalCommits = commits.size();        
        int i = 0;
        Integer onePorcent = 1;
        if (totalCommits >= 100) {
            onePorcent = totalCommits / 100;
        }
        for (Commit commit : commits) {
            analyseCommit(commit);
            i++;
            if ((i % onePorcent == 0)) {
                notifyObservers(totalCommits, i);
            }
        }
        notifyObservers(totalCommits, i);
        detectedSmellDao.insertDetectedSmells(detectedSmells, connection);
    }

    private void analyseCommit(Commit commit) throws Exception {
        Log.writeLog("Analysing commit " + commit.getHash());
        final CompleteJavaFileCreator creator = new CompleteJavaFileCreator(
                new String[]{commit.getLocalPath()}, new String[]{""},
                new String[]{commit.getLocalPath()});
        final ICodeLevelModel codeLevelModel = Factory.getInstance()
                .createCodeLevelModel(commit.getHash());
        codeLevelModel.create(creator);

        // try {
        final padl.creator.javafile.eclipse.astVisitors.LOCModelAnnotator annotator2 = new padl.creator.javafile.eclipse.astVisitors.LOCModelAnnotator(
                codeLevelModel);
        creator.applyAnnotator(annotator2);

        final padl.creator.javafile.eclipse.astVisitors.ConditionalModelAnnotator annotator1 = new padl.creator.javafile.eclipse.astVisitors.ConditionalModelAnnotator(
                codeLevelModel);
        creator.applyAnnotator(annotator1);

        final IIdiomLevelModel idiomLevelModel = (IIdiomLevelModel) new AACRelationshipsAnalysis()
                .invoke(codeLevelModel);

        analyseCodeLevelModel(idiomLevelModel, commit);
    }

    public final void analyseCodeLevelModel(final IIdiomLevelModel idiomLevelModel, Commit commit) throws Exception {
        try {
            for (int i = 0; i < smells.size(); i++) {
                final String antipatternName = smells.get(i).toString();

                final Class<?> detectionClass = Class
                        .forName("sad.designsmell.detection.repository."
                                + antipatternName + '.' + antipatternName
                                + "Detection");
                final IDesignSmellDetection detection = (IDesignSmellDetection) detectionClass
                        .newInstance();

                detection.detect(idiomLevelModel);
                List<DetectedSmell> smellArquivo = new ArrayList<>();
                for (Object s : detection.getDesignSmells()) {
                    DesignSmell ds = (DesignSmell) s;
                    for (Object o : ds.listOfCodeSmells()) {
                        ICodeSmell cs = (ICodeSmell) o;
                        DetectedSmell sa = processa(cs, ds.getName(), commit);
                        if (sa != null) {
                            smellArquivo.add(sa);
                        }
                    }
                }
                //detectedSmells.put(key, value);
            }
        } catch (final Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private DetectedSmell processa(ICodeSmell cs, String name, Commit commit) throws Exception {
        if (cs instanceof CodeSmell) {
            return new DetectedSmell(SmellEnum.getSmellName(name), new ClassCommitChange(null, null));
        } else if (cs instanceof CodeSmellComposite) {
            CodeSmellComposite co = (CodeSmellComposite) cs;
            for (Object o : co.getSetOfCodeSmellsOfGeneric()) {
                ICodeSmell ics = (ICodeSmell) o;
                return processa(ics, name, commit);
            }
        }
        throw new Exception("Cant find cs instance " + cs);
    }

    private void notifyObservers(int totalCommits, int commitsPerformed) {
        Double d = new Double(commitsPerformed) / new Double(totalCommits);
        int progress = new Double(d * 100).intValue();
        observer.sendCurrentPercent(progress);
        observer.sendStatusMessage("Analysed " + commitsPerformed + " commits of " + totalCommits);
        Log.writeLog("Analysed " + commitsPerformed + " commits of " + totalCommits);
    }

}
