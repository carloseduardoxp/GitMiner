package miner.model.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import miner.model.dao.ClassCommitChangeDao;
import miner.model.dao.DetectedSmellDao;
import miner.model.dao.ProjectDao;
import miner.model.dao.structure.DaoFactory;
import miner.model.domain.Branch;
import miner.model.domain.ClassCommitChange;
import miner.model.domain.Commit;
import miner.model.domain.CommitChange;
import miner.model.domain.DetectedSmell;
import miner.model.domain.Project;
import miner.model.domain.SmellEnum;
import miner.util.Log;
import miner.util.exception.ValidationException;
import padl.analysis.repository.AACRelationshipsAnalysis;
import padl.creator.javafile.eclipse.CompleteJavaFileCreator;
import padl.kernel.IClass;
import padl.kernel.ICodeLevelModel;
import padl.kernel.IEntity;
import padl.kernel.IIdiomLevelModel;
import padl.kernel.impl.Factory;
import pom.metrics.IUnaryMetric;
import pom.metrics.MetricsRepository;
import sad.designsmell.detection.IDesignSmellDetection;
import sad.kernel.ICodeSmell;
import sad.kernel.impl.CodeSmell;
import sad.kernel.impl.CodeSmellComposite;
import sad.kernel.impl.DesignSmell;

public class ImportCodeSmellsService {

    private static DetectedSmellDao detectedSmellDao;
    private final ClassCommitChangeDao classCommitChangeDao;
    private final ProjectDao projectDao;
    private final Observer observer;
    private Project project;
    private final List<SmellEnum> smells;
    private CurrentClasses currentClasses;

    public ImportCodeSmellsService(Observer observer, Project project, List<SmellEnum> smells) {
        detectedSmellDao = DaoFactory.getDetectedSmellDao();
        classCommitChangeDao = DaoFactory.getClassCommitChangeDao();
        projectDao = DaoFactory.getProjectDao();
        this.observer = observer;
        this.project = project;
        this.smells = smells;
        this.currentClasses = new CurrentClasses();
    }

    public void execute() throws Exception {
        try {
            Log.writeLog(project.getName(), "ImportSmells", "Starting Import Smells of Project " + project.getName());
            Log.writeLog("Mounting Project (branches, commits and commit changes) " + project.getName());
            observer.sendStatusMessage("Mounting Project (branches, commits and commit changes) " + project.getName());
            project = projectDao.mountProject(project);
            for (Branch branch : project.getBranches()) {
                analyseBranch(branch);
            }
            Log.writeLog("Done.");
        } catch (Exception e) {
            e.printStackTrace();
            Log.writeLog("Error Exception ocurred " + e.getMessage());
            throw new Exception(e);
        }
    }

    private void analyseBranch(Branch branch) throws Exception {
        Log.writeLog("Analysing branch " + branch.getName());
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
    }
    
    private void analyseCommit(Commit commit) throws ValidationException, IOException, Exception {
		Log.writeLog("Analysing commit " + commit.getHash());
		if (commit.getChanges().isEmpty()) {
			Log.writeLog("Commit " + commit.getHash()+" dont have changes");
			return;
		}
		boolean isChangeAnyone = currentClasses.addCommit(commit);
		if (currentClasses.getPaths().length == 0 || !isChangeAnyone) {
			Log.writeLog("Commit "+ commit.getHash()+" dont have changes in analysed classes");
			return;
		}
		IIdiomLevelModel iIdiomLevelModel = analyseCodeLevelModelFromJavaSourceFiles(commit.getBranch().getLocalPathCommits(),commit.getHash());
		getMetrics(commit,iIdiomLevelModel);
		classCommitChangeDao.updateMetrics(commit);		
		List<DetectedSmell> smells = analyseCodeLevelModel(iIdiomLevelModel,commit);
		if (!smells.isEmpty()) {
			Log.writeLog("Find smells: "+smells);
			detectedSmellDao.insertDetectedSmells(smells);	
		} else {
			Log.writeLog("Cant Find smells");
		}
	}

	public IIdiomLevelModel analyseCodeLevelModelFromJavaSourceFiles(String path, String name) throws Exception {		
		Log.writeLog("Count current classes analysed: "+currentClasses.getPaths().length);
		Log.writeLog("Current classes analysed: "+Arrays.asList(currentClasses.getPaths()));
		
		final CompleteJavaFileCreator creator = new CompleteJavaFileCreator(new String[] { path }, new String[] { "" },
				currentClasses.getPaths());
		final ICodeLevelModel codeLevelModel = Factory.getInstance().createCodeLevelModel(name);
		codeLevelModel.create(creator);
		final padl.creator.javafile.eclipse.astVisitors.LOCModelAnnotator annotator2 = new padl.creator.javafile.eclipse.astVisitors.LOCModelAnnotator(
				codeLevelModel);
		creator.applyAnnotator(annotator2);

		final padl.creator.javafile.eclipse.astVisitors.ConditionalModelAnnotator annotator1 = new padl.creator.javafile.eclipse.astVisitors.ConditionalModelAnnotator(
				codeLevelModel);
		creator.applyAnnotator(annotator1);

		return (IIdiomLevelModel) new AACRelationshipsAnalysis()
				.invoke(codeLevelModel);
	}

    public final List<DetectedSmell> analyseCodeLevelModel(final IIdiomLevelModel idiomLevelModel, Commit commit) throws Exception {
    	List<DetectedSmell> smellArquivo = new ArrayList<>();
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
            }
        } catch (final Exception e) {
            e.printStackTrace();
            throw e;
        }
        return smellArquivo;
    }

    private DetectedSmell processa(ICodeSmell cs, String name, Commit commit) throws Exception {
        if (cs instanceof CodeSmell) {
        	ClassCommitChange classCommitChange = getClassCommitChange(commit.getChanges(),cs.getIClass());
			if (classCommitChange == null) {
				return null;
			}
            return new DetectedSmell(SmellEnum.getSmellName(name),classCommitChange,cs.toString() );
        } else if (cs instanceof CodeSmellComposite) {
            CodeSmellComposite co = (CodeSmellComposite) cs;
            for (Object o : co.getSetOfCodeSmellsOfGeneric()) {
                ICodeSmell ics = (ICodeSmell) o;
                return processa(ics, name, commit);
            }
        }
        throw new Exception("Cant find cs instance " + cs);
    }
    
	private static ClassCommitChange getClassCommitChange(List<CommitChange> changes, IClass aClass) throws ValidationException {
		for (CommitChange change: changes) {
			for (ClassCommitChange classChange: change.getClassCommitchange()) {				
				if (classChange.getJavaClass().getName().equals(aClass.getDisplayID())) {
					return classChange;
				}
 			}
		}
		//throw new ValidationException("Cant found class "+aClass.getDisplayID());
		return null;
	}
	
	private static void getMetrics(Commit commit,IIdiomLevelModel iIdiomLevelModel) throws ValidationException {
		final Iterator iter = iIdiomLevelModel.getIteratorOnTopLevelEntities();
		while (iter.hasNext()) {
			final IEntity entity = (IEntity) iter.next();
			if (entity instanceof IClass) {
				final IClass aClass = (IClass) entity;
				ClassCommitChange classCommitChange = getClassCommitChange(commit.getChanges(),aClass);
				if (classCommitChange == null) {
					continue;
				}
				classCommitChange.setACAIC(((IUnaryMetric) MetricsRepository.getInstance().getMetric("ACAIC")).compute(iIdiomLevelModel, aClass));				
				classCommitChange.setACMIC(((IUnaryMetric) MetricsRepository.getInstance().getMetric("ACMIC")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setAID(((IUnaryMetric) MetricsRepository.getInstance().getMetric("AID")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setANA(((IUnaryMetric) MetricsRepository.getInstance().getMetric("ANA")).compute(iIdiomLevelModel, aClass));				
				classCommitChange.setCAM(((IUnaryMetric) MetricsRepository.getInstance().getMetric("CAM")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setCBO(((IUnaryMetric) MetricsRepository.getInstance().getMetric("CBO")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setCIS(((IUnaryMetric) MetricsRepository.getInstance().getMetric("CIS")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setCLD(((IUnaryMetric) MetricsRepository.getInstance().getMetric("CLD")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setCP(((IUnaryMetric) MetricsRepository.getInstance().getMetric("CP")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setDAM(((IUnaryMetric) MetricsRepository.getInstance().getMetric("DAM")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setDCAEC(((IUnaryMetric) MetricsRepository.getInstance().getMetric("DCAEC")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setDCC(((IUnaryMetric) MetricsRepository.getInstance().getMetric("DCC")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setDCMEC(((IUnaryMetric) MetricsRepository.getInstance().getMetric("DCMEC")).compute(iIdiomLevelModel, aClass));				
				classCommitChange.setDIT(((IUnaryMetric) MetricsRepository.getInstance().getMetric("DIT")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setDSC(((IUnaryMetric) MetricsRepository.getInstance().getMetric("DSC")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setEIC(((IUnaryMetric) MetricsRepository.getInstance().getMetric("EIC")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setEIP(((IUnaryMetric) MetricsRepository.getInstance().getMetric("EIP")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setICH(((IUnaryMetric) MetricsRepository.getInstance().getMetric("ICHClass")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setIR(((IUnaryMetric) MetricsRepository.getInstance().getMetric("IR")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setLCOM1(((IUnaryMetric) MetricsRepository.getInstance().getMetric("LCOM1")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setLCOM2(((IUnaryMetric) MetricsRepository.getInstance().getMetric("LCOM2")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setLCOM5(((IUnaryMetric) MetricsRepository.getInstance().getMetric("LCOM5")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setLOC(((IUnaryMetric) MetricsRepository.getInstance().getMetric("LOC")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setMcCabe(((IUnaryMetric) MetricsRepository.getInstance().getMetric("McCabe")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setMFA(((IUnaryMetric) MetricsRepository.getInstance().getMetric("MFA")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setMOA(((IUnaryMetric) MetricsRepository.getInstance().getMetric("MOA")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setNAD(((IUnaryMetric) MetricsRepository.getInstance().getMetric("NAD")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setNCM(((IUnaryMetric) MetricsRepository.getInstance().getMetric("NCM")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setNCP(((IUnaryMetric) MetricsRepository.getInstance().getMetric("NCP")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setNMA(((IUnaryMetric) MetricsRepository.getInstance().getMetric("NMA")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setNMD(((IUnaryMetric) MetricsRepository.getInstance().getMetric("NMD")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setNMI(((IUnaryMetric) MetricsRepository.getInstance().getMetric("NMI")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setNMO(((IUnaryMetric) MetricsRepository.getInstance().getMetric("NMO")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setNOA(((IUnaryMetric) MetricsRepository.getInstance().getMetric("NOA")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setNOC(((IUnaryMetric) MetricsRepository.getInstance().getMetric("NOC")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setNOD(((IUnaryMetric) MetricsRepository.getInstance().getMetric("NOD")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setNOF(((IUnaryMetric) MetricsRepository.getInstance().getMetric("NOF")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setNOH(((IUnaryMetric) MetricsRepository.getInstance().getMetric("NOH")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setNOM(((IUnaryMetric) MetricsRepository.getInstance().getMetric("NOM")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setNOP(((IUnaryMetric) MetricsRepository.getInstance().getMetric("NOP")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setNOPM(((IUnaryMetric) MetricsRepository.getInstance().getMetric("NOPM")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setNOTC(((IUnaryMetric) MetricsRepository.getInstance().getMetric("NOTC")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setNOTI(((IUnaryMetric) MetricsRepository.getInstance().getMetric("NOTI")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setNPrM(((IUnaryMetric) MetricsRepository.getInstance().getMetric("NPrM")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setPIIR(((IUnaryMetric) MetricsRepository.getInstance().getMetric("PIIR")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setPP(((IUnaryMetric) MetricsRepository.getInstance().getMetric("PP")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setREIP(((IUnaryMetric) MetricsRepository.getInstance().getMetric("REIP")).compute(iIdiomLevelModel, aClass));			
				classCommitChange.setRFC(((IUnaryMetric) MetricsRepository.getInstance().getMetric("RFC")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setRFP(((IUnaryMetric) MetricsRepository.getInstance().getMetric("RFP")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setRPII(((IUnaryMetric) MetricsRepository.getInstance().getMetric("RPII")).compute(iIdiomLevelModel, aClass));
				//classCommitChange.setRRFP(((IUnaryMetric) MetricsRepository.getInstance().getMetric("RRFP")).compute(iIdiomLevelModel, aClass)); //lento
				//classCommitChange.setRRTP(((IUnaryMetric) MetricsRepository.getInstance().getMetric("RRTP")).compute(iIdiomLevelModel, aClass)); //lento
				classCommitChange.setRTP(((IUnaryMetric) MetricsRepository.getInstance().getMetric("RTP")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setSIX(((IUnaryMetric) MetricsRepository.getInstance().getMetric("SIX")).compute(iIdiomLevelModel, aClass));
				classCommitChange.setWMC(((IUnaryMetric) MetricsRepository.getInstance().getMetric("WMC")).compute(iIdiomLevelModel, aClass));	
			}
		}
//		if (changes.size() > 0) {
//			throw new ValidationException("Cant found classes "+changes);
//		}
	}

    private void notifyObservers(int totalCommits, int commitsPerformed) {
        Double d = new Double(commitsPerformed) / new Double(totalCommits);
        int progress = new Double(d * 100).intValue();
        observer.sendCurrentPercent(progress);
        observer.sendStatusMessage("Analysed " + commitsPerformed + " commits of " + totalCommits);
        Log.writeLog("Analysed " + commitsPerformed + " commits of " + totalCommits);
    }

}
