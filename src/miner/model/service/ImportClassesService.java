package miner.model.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import miner.model.dao.ClassDao;
import miner.model.dao.ProjectDao;
import miner.model.dao.structure.DaoFactory;
import miner.model.dao.structure.JdbcConnection;
import miner.model.domain.Branch;
import miner.model.domain.Commit;
import miner.model.domain.CommitChange;
import miner.model.domain.Project;
import miner.util.Log;
import miner.util.exception.ConnectionException;
import miner.util.exception.ValidationException;
import padl.analysis.repository.AACRelationshipsAnalysis;
import padl.creator.javafile.eclipse.CompleteJavaFileCreator;
import padl.kernel.IClass;
import padl.kernel.ICodeLevelModel;
import padl.kernel.IEntity;
import padl.kernel.IEnum;
import padl.kernel.IIdiomLevelModel;
import padl.kernel.IInterface;
import padl.kernel.impl.Factory;

public class ImportClassesService {

	private static ClassDao classDao;
	private final ProjectDao projectDao;
	private final Observer observer;
	private Project project;
	private Map<miner.model.domain.Class,List<CommitChange>> classCommitChanges;
	private Connection connection;
	private List<String> filters;
	private List<String> interfaces;
	private List<String> enums;

	public ImportClassesService(Observer observer, Project project,List<String> filters) {
		classDao = DaoFactory.getClassDao();
		projectDao = DaoFactory.getProjectDao();
		this.observer = observer;
		this.project = project;
		this.filters = filters;
		this.interfaces = new ArrayList<>();
		this.enums = new ArrayList<>();
	}

	public void execute() throws Exception {
		try {
			Log.writeLog(project.getName(), "ImportClasses", "Starting Import Classes of Project " + project.getName());
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

	private void analyseBranch(Branch branch)
			throws ValidationException, IOException, ConnectionException, SQLException, Exception {
		Log.writeLog("Analysing branch " + branch.getName());
		this.classCommitChanges = new HashMap<>();
		List<Commit> commits = branch.getCommits();
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
		if (classCommitChanges.keySet().size() > 0) {
			classDao.insertClasses(branch, classCommitChanges, connection);
		}
	}

	private void notifyObservers(int totalCommits, int commitsPerformed) {
		Double d = new Double(commitsPerformed) / new Double(totalCommits);
		int progress = new Double(d * 100).intValue();
		observer.sendCurrentPercent(progress);
		observer.sendStatusMessage("Analysed " + commitsPerformed + " commits of " + totalCommits);
		Log.writeLog("Analysed " + commitsPerformed + " commits of " + totalCommits);
	}

	private void analyseCommit(Commit commit) throws ValidationException, IOException, Exception {
		Log.writeLog("Analysing commit " + commit.getHash());
		for (CommitChange commitChange : commit.getChanges()) {
			String classPath = commitChange.getLocalPath();
			String path = commitChange.getCommit().getLocalPathCommits();
			String fileName = commitChange.getNewFileName();
			if (fileName.endsWith(".jar")) {
				Log.writeLog("Cant analyse jars " + fileName + " - " +classPath);
				continue;
			}
			try {
				List<String> classNames = analyseCodeLevelModelFromJavaSourceFiles(path, classPath,fileName);
				if (classNames.isEmpty()) {
					Log.writeLog("Cant find java classes in path " + path);
				}
				for (String className: classNames) {
					miner.model.domain.Class javaClass = 
							new miner.model.domain.Class(className,commit.getBranch(),analyseClass(fileName),interfaces.contains(className),enums.contains(className));					
					if (classCommitChanges.containsKey(javaClass)) {
						List<CommitChange> changes = classCommitChanges.get(javaClass);
						changes.add(commitChange);
						classCommitChanges.put(javaClass, changes);
					} else {
						classCommitChanges.put(javaClass, new ArrayList<>(Arrays.asList(commitChange)));
					}					
				}
			} catch (Exception ve) {
				ve.printStackTrace();
				Log.writeLog("Cant found class name in change " + commitChange.toString());
				throw new ValidationException("Cant found class name in change " + commitChange.toString());
			}
		}
	}

	private Boolean analyseClass(String className) {
		for (String filter: filters) {
			if (className.contains(filter)) {
				return false;
			}
		}
		return true;
	}

	public List<String> analyseCodeLevelModelFromJavaSourceFiles(String path, String classPath,String name) throws Exception {		
		final CompleteJavaFileCreator creator = new CompleteJavaFileCreator(new String[] { path }, new String[] { "" },
				new String[] { classPath });
		final ICodeLevelModel codeLevelModel = Factory.getInstance().createCodeLevelModel(name);
		codeLevelModel.create(creator);
		final padl.creator.javafile.eclipse.astVisitors.LOCModelAnnotator annotator2 = new padl.creator.javafile.eclipse.astVisitors.LOCModelAnnotator(
				codeLevelModel);
		creator.applyAnnotator(annotator2);

		final padl.creator.javafile.eclipse.astVisitors.ConditionalModelAnnotator annotator1 = new padl.creator.javafile.eclipse.astVisitors.ConditionalModelAnnotator(
				codeLevelModel);
		creator.applyAnnotator(annotator1);

		final IIdiomLevelModel idiomLevelModel = (IIdiomLevelModel) new AACRelationshipsAnalysis()
				.invoke(codeLevelModel);
		return getClassName(idiomLevelModel);
	}

	private List<String> getClassName(IIdiomLevelModel anAbstractLevelModel) {
		final Iterator iter = anAbstractLevelModel.getIteratorOnTopLevelEntities();
		List<String> classNames = new ArrayList<>();
		while (iter.hasNext()) {
			final IEntity entity = (IEntity) iter.next();
			if (entity instanceof IClass) {
				IClass aClass = (IClass) entity;
				//aClass.getLocalPath();
				classNames.add(aClass.getDisplayID());	
			} else if (entity instanceof IEnum) {
				IEnum aEnum = (IEnum) entity;
				enums.add(aEnum.getDisplayID());
				classNames.add(aEnum.getDisplayID());						
			} else if (entity instanceof IInterface) {
				IInterface aInterface = (IInterface) entity;
				interfaces.add(aInterface.getDisplayID());
				classNames.add(aInterface.getDisplayID());
			} 
			
		}
		return classNames;
	}

}
