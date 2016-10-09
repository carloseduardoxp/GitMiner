package miner.model.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry.ChangeType;

import jxl.CellView;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import miner.model.dao.ProjectDao;
import miner.model.dao.structure.DaoFactory;
import miner.model.domain.Branch;
import miner.model.domain.Class;
import miner.model.domain.ClassCommitChange;
import miner.model.domain.Commit;
import miner.model.domain.CommitChange;
import miner.model.domain.DetectedSmell;
import miner.model.domain.Project;
import miner.model.domain.SmellEnum;
import miner.util.Log;

public class ClassSmellMetricsVersionService {

	private Project project;

	private Observer observer;

	private ProjectDao projectDao;

	private WritableWorkbook workbook;

	private WritableSheet writableSheetClass;
	
	private WritableSheet writableSheetSmell;
	
	private List<SmellReport> smellsReport;
	
	private List<SmellEnum> currentSmells;
	
	private List<SmellEnum> smellsRemovedClass;
	
	private Map<SmellEnum,Integer> versions;

	public ClassSmellMetricsVersionService(Observer observer, Project project)
			throws FileNotFoundException, BiffException, IOException {
		projectDao = DaoFactory.getProjectDao();
		this.observer = observer;
		this.project = project;
		currentSmells = new ArrayList<>();
		String fileName = project.getLocalPathWeka() + "SmellsMetrics.xls";
		workbook = Workbook.createWorkbook(new File(fileName));
		writableSheetSmell = workbook.createSheet("Smells", 0);
		writableSheetClass = workbook.createSheet("Class", 1);
		smellsReport = new ArrayList<>();
		smellsRemovedClass = new ArrayList<>();		
	}

	public void execute() throws Exception {
		try {
			Log.writeLog(project.getName(), "ClassSmellMetricsVersionService",
					"Starting Generating Report Inter Relation Smells Service " + project.getName());
			Log.writeLog("Mounting Project (branches, commits and commit changes) " + project.getName());
			observer.sendStatusMessage("Mounting Project (branches, commits and commit changes) " + project.getName());
			project = projectDao.mountProject(project, true);
			for (Branch branch : project.getBranches()) {
				analyseBranch(branch);
			}
			writeSmellReport();
			expandColumn(writableSheetClass);
			expandColumn(writableSheetSmell);
			workbook.write();
			workbook.close();
			Log.writeLog("Done.");
		} catch (Exception e) {
			e.printStackTrace();
			Log.writeLog("Error Exception ocurred " + e.getMessage());
			throw new Exception(e);
		}
	}

	private void writeSmellReport() throws RowsExceededException,WriteException {
		int row = 0;
		int column = 0;
		writableSheetSmell.addCell(new Label(column++, row, "Smells"));
		writableSheetSmell.addCell(new Label(column++, row, "Total Versions"));
		writableSheetSmell.addCell(new Label(column++, row, "Qt Removed Smell"));
		writableSheetSmell.addCell(new Label(column++, row, "Qt Smell return back"));
		writableSheetSmell.addCell(new Label(column++, row, "Qt Class Deleted"));
		writableSheetSmell.addCell(new Label(column++, row, "Qt Last commit with smell"));
		writableSheetSmell.addCell(new Label(column++, row, "Qt Versions to delete class"));
		writableSheetSmell.addCell(new Label(column++, row, "Qt Versions to Remove smell"));
		writableSheetSmell.addCell(new Label(column++, row, "Smells Removed Together"));
		writableSheetSmell.addCell(new Label(column++, row, "Smells Returned back Together"));		
		writableSheetSmell.addCell(new Label(column++, row, "Smells Deleted Together"));
		writableSheetSmell.addCell(new Label(column++, row, "Smells Last commit Together"));
		for (SmellReport smellReport: smellsReport) {
			column = 0;
			row++;	
			writableSheetSmell.addCell(new Label(column++, row, smellReport.getSmell().toString()));
			writableSheetSmell.addCell(new Label(column++, row, smellReport.getTotalVersions().toString()));
			writableSheetSmell.addCell(new Label(column++, row, smellReport.getRemoved().toString()));
			writableSheetSmell.addCell(new Label(column++, row, smellReport.getReturned().toString()));
			writableSheetSmell.addCell(new Label(column++, row, smellReport.getDeleted().toString()));
			writableSheetSmell.addCell(new Label(column++, row, smellReport.getFinishedWithSmell().toString()));
			writableSheetSmell.addCell(new Label(column++, row, smellReport.getVersionsStayedDeleted().toString()));
			writableSheetSmell.addCell(new Label(column++, row, smellReport.getVersionsStayedRemoved().toString()));
			writableSheetSmell.addCell(new Label(column++, row, smellReport.getRemovedTogether().toString()));
			writableSheetSmell.addCell(new Label(column++, row, smellReport.getReturnedTogether().toString()));
			writableSheetSmell.addCell(new Label(column++, row, smellReport.getDeletedTogether().toString()));
			writableSheetSmell.addCell(new Label(column++, row, smellReport.getFinishedTogether().toString()));
		}
		
	}

	private void expandColumn(WritableSheet sheet) {
		for (int x = 0; x < sheet.getColumns(); x++) {
			CellView cell = sheet.getColumnView(x);
			cell.setAutosize(true);
			sheet.setColumnView(x, cell);
		}
	}

	private void analyseBranch(Branch branch) throws WriteException {
		Log.writeLog("Analysing branch " + branch.getName());
		List<Class> classes = branch.getClasses();
		int totalClasses = classes.size();
		int i = 0;
		Integer onePorcent = 1;
		if (totalClasses >= 100) {
			onePorcent = totalClasses / 100;
		}
		for (Class javaClass : classes) {
			analyseClass(javaClass, i);
			i++;
			if ((i % onePorcent == 0)) {
				notifyObservers(totalClasses, i);
			}
		}
		if (totalClasses > 0) {
			notifyObservers(totalClasses, i);
		}
	}

	private void analyseClass(Class javaClass, int column) throws RowsExceededException, WriteException {
		int row = 0;
		WritableCell cell = new Label(column, row, javaClass.getName());
		writableSheetClass.addCell(cell);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
		Log.writeLog("Analysing class " + javaClass.getName());		
		List<ClassCommitChange> changes = javaClass.getChanges();
		for (ClassCommitChange ccc : changes) {
			CommitChange cc = ccc.getCommitChange();
			Commit c = cc.getCommit();
			String date = sdf.format(c.getCommitterDate());
			String operation = cc.getChangeType().toString();
			String hash = c.getHash().substring(0, 7);
			String loc = ccc.getLOC() + "";
			String cbo = ccc.getCBO() + "";
			String rfc = ccc.getRFC() + "";
			String smells = getSmells(ccc);
			analyseSmells(row==0?true:false,ccc.getSmells(),cc.getChangeType() == ChangeType.DELETE?true:false);
			row++;
			cell = new Label(column, row, date + " - " + hash + " - " + operation + " - " + cc.getId() + " - LOC " + loc + " CBO " + cbo
					+ " RFC " + rfc + " " + smells);
			writableSheetClass.addCell(cell);
		}
	}

	private void analyseSmells(boolean newClass, List<DetectedSmell> smells,boolean deleteCommit) {
		if (newClass) {
			for (SmellEnum s: currentSmells) {
				SmellReport smellReport = getSmellReport(s);
				smellReport.addFinishedWithSmell();
				smellReport.addFinishedTogether(getOtherSmells(currentSmells,s));
			}
			currentSmells = new ArrayList<>();
			smellsRemovedClass = new ArrayList<>();
			versions = new HashMap<>();
		}
		List<SmellEnum> smellList = getSmells(smells);
		List<SmellEnum> smellsRemoved = getSmellsRemoved(smellList);
		for (DetectedSmell smell: smells) {
			int quantos = 1;
			SmellEnum sEnum = smell.getSmell();
			if (versions.containsKey(sEnum)) {
				quantos = versions.get(sEnum)+1;
			}
			versions.put(sEnum,quantos);
			SmellReport smellReport = getSmellReport(sEnum);
			smellReport.addTotalVersions();
		}
		for (SmellEnum s: smellsRemoved) {
			SmellReport smellReport = getSmellReport(s);
			if (deleteCommit) {
				smellReport.addDeleted();
				smellReport.addDeletedTogether(getOtherSmells(smellsRemoved,s));
				smellReport.addVersionsStayedDeleted(versions.get(s));
			} else {
				smellsRemovedClass.add(s);
				smellReport.addRemoved();	
				smellReport.addRemovedTogether(getOtherSmells(smellsRemoved,s));
				smellReport.addVersionsStayedRemoved(versions.get(s));
			}						
			versions.remove(s);
		}
		List<SmellEnum> smellsReturned = getSmellsReturned(smellList);
		for (SmellEnum s: smellsReturned) {
			SmellReport smellReport = getSmellReport(s);
			smellReport.addReturned();
			smellReport.addReturnedTogether(getOtherSmells(smellsReturned,s));
			smellsRemovedClass.remove(s);
		}
		currentSmells = smellList;		
	}

	private List<SmellEnum> getOtherSmells(List<SmellEnum> smellsRemoved, SmellEnum s) {
		List<SmellEnum> retorno = new ArrayList<>();
		for (SmellEnum smell: smellsRemoved) {
			if (!smell.equals(s)) {
				retorno.add(smell);
			}
		}
		return retorno;
	}

	private SmellReport getSmellReport(SmellEnum s) {
		SmellReport smellReport = new SmellReport(s);
		if (smellsReport.contains(smellReport)) {
			return smellsReport.get(smellsReport.indexOf(smellReport));
		}
		smellsReport.add(smellReport);
		return smellReport;
	}

	private List<SmellEnum> getSmellsRemoved(List<SmellEnum> smells) {
		List<SmellEnum> smellsRemoved = new ArrayList<>();
		for (SmellEnum smell: currentSmells) {
			if (!smells.contains(smell)) {
				smellsRemoved.add(smell);
			}
		}
		return smellsRemoved;
	}
	
	private List<SmellEnum> getSmellsReturned(List<SmellEnum> smells) {
		List<SmellEnum> smellsReturned = new ArrayList<>();
		for (SmellEnum smell: smells) {
			if (!currentSmells.contains(smell) && smellsRemovedClass.contains(smell)) {
				smellsReturned.add(smell);
			}
		}
		return smellsReturned;
	}


	private List<SmellEnum> getSmells(List<DetectedSmell> smells) {
		List<SmellEnum> smellsReturn = new ArrayList<>();
		for (DetectedSmell smell: smells) {
			smellsReturn.add(smell.getSmell());
		}
		return smellsReturn;
	}

	private String getSmells(ClassCommitChange change) {
		String value = "";
		List<DetectedSmell> smells = change.getSmells();
		for (DetectedSmell smell : smells) {
			value += smell.getSmell().name() + ",";
		}
		if (value.equals("")) {
			return "";
		}
		return value.substring(0, value.length() - 1);
	}

	private void notifyObservers(int totalCommits, int commitsPerformed) {
		Double d = new Double(commitsPerformed) / new Double(totalCommits);
		int progress = new Double(d * 100).intValue();
		observer.sendCurrentPercent(progress);
		observer.sendStatusMessage("Analysed " + commitsPerformed + " classes of " + totalCommits);
		Log.writeLog("Analysed " + commitsPerformed + " classes of " + totalCommits);
	}

}

class SmellReport {
	private SmellEnum smell;
	
	private Integer removed;
	
	private Integer returned;
	
	private Integer deleted;
	
	private Integer totalVersions;
	
	private Integer finishedWithSmell;
	
	private List<Integer> versionsStayedRemoved;
	
	private List<Integer> versionsStayedDeleted;
	
	private Map<SmellEnum, Integer> removedTogether;
	
	private Map<SmellEnum,Integer> returnedTogether;
	
	private Map<SmellEnum,Integer> deletedTogether;
	
	private Map<SmellEnum,Integer> finishedTogether;
	
	public SmellReport(SmellEnum smell) {
		this.smell = smell;
		removed = 0;
		returned = 0;
		deleted = 0;
		totalVersions = 0;
		finishedWithSmell = 0;
		versionsStayedRemoved = new ArrayList<>();
		versionsStayedDeleted = new ArrayList<>();
		removedTogether = new HashMap<>();
		returnedTogether = new HashMap<>();
		deletedTogether = new HashMap<>();
		finishedTogether = new HashMap<>();
	}

	public SmellEnum getSmell() {
		return smell;
	}

	public Integer getRemoved() {
		return removed;
	}
	
	public void addRemoved() {
		removed++;
	}
	
	public Integer getTotalVersions() {
		return totalVersions;
	}
	
	public void addTotalVersions() {
		totalVersions++;
	}

	public Integer getFinishedWithSmell() {
		return finishedWithSmell;
	}
	
	public void addFinishedWithSmell() {
		finishedWithSmell++;
	}

	public Integer getReturned() {
		return returned;
	}
	
	public void addReturned() {
		returned++;
	}

	public List<Integer> getVersionsStayedRemoved() {
		return versionsStayedRemoved;
	}
	
	public void addVersionsStayedRemoved(Integer qtd) {
		versionsStayedRemoved.add(qtd);
	}
	
	public List<Integer> getVersionsStayedDeleted() {
		return versionsStayedDeleted;
	}
	
	public void addVersionsStayedDeleted(Integer qtd) {
		versionsStayedDeleted.add(qtd);
	}


	public Map<SmellEnum, Integer> getRemovedTogether() {
		return removedTogether;
	}
	
	public Map<SmellEnum, Integer> getDeletedTogether() {
		return deletedTogether;
	}
	
	public void addRemovedTogether(List<SmellEnum> smells) {
		for (SmellEnum smell: smells) {
			Integer quantos = 1;
			if (removedTogether.containsKey(smell)) {
				quantos = removedTogether.get(smell)+1;
			}
			removedTogether.put(smell,quantos);
		}
	}

	public Map<SmellEnum, Integer> getReturnedTogether() {
		return returnedTogether;
	}
	
	public void addReturnedTogether(List<SmellEnum> smells) {
		for (SmellEnum smell: smells) {
			Integer quantos = 1;
			if (returnedTogether.containsKey(smell)) {
				quantos = returnedTogether.get(smell)+1;
			}
			returnedTogether.put(smell,quantos);
		}
	}
	
	public void addDeletedTogether(List<SmellEnum> smells) {
		for (SmellEnum smell: smells) {
			Integer quantos = 1;
			if (deletedTogether.containsKey(smell)) {
				quantos = deletedTogether.get(smell)+1;
			}
			deletedTogether.put(smell,quantos);
		}
	}
	
	public Map<SmellEnum, Integer> getFinishedTogether() {
		return finishedTogether;
	}
	
	public void addFinishedTogether(List<SmellEnum> smells) {
		for (SmellEnum smell: smells) {
			Integer quantos = 1;
			if (finishedTogether.containsKey(smell)) {
				quantos = finishedTogether.get(smell)+1;
			}
			finishedTogether.put(smell,quantos);
		}
	}

	public Integer getDeleted() {
		return deleted;
	}
	
	public void addDeleted() {
		deleted++;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((smell == null) ? 0 : smell.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SmellReport other = (SmellReport) obj;
		if (smell != other.smell)
			return false;
		return true;
	}
	
}

