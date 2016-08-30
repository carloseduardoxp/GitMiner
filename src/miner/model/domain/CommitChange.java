package miner.model.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import miner.util.Log;

public class CommitChange {

    private Integer id;

    private FileModificationTypeEnum modificationType;    

    private String fileName;

    private Commit commit;

    private Class javaClass;    
    
    private Integer totalLines;
    
    private List<ClassCommitChange> classCommitchange;

    public CommitChange() {
    }
    
    public CommitChange(Integer id) {
        this.id = id;
    }

    public CommitChange(FileModificationTypeEnum modificationType, String fileName, Commit commit) {
        super();
        this.commit = commit;
        this.modificationType = modificationType;
        this.fileName = fileName;
    }

    public static List<CommitChange> getChanges(Commit commit) throws Exception {
        String command = "git --git-dir=" + commit.getBranch().getLocalPathDownloads()+ "/.git --work-tree="
                + commit.getBranch().getLocalPathDownloads()+ " show --pretty=format: --name-status "
                + commit.getHash();
        Log.writeLog("Exec git command to get files changed in commit " + commit.getHash());
        Process powerShellProcess = Runtime.getRuntime().exec(command);
        powerShellProcess.getOutputStream().close();
        List<CommitChange> changes = new ArrayList<>();
        String line;
        BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));
        while ((line = stdout.readLine()) != null) {
            if (!line.equals("")) {
                Log.writeLog("Return " + line);
                String[] values = line.split("\t");
                String modificationType = values[0];
                String fileName = values[1];
                changes.add(new CommitChange(FileModificationTypeEnum.getType(modificationType), fileName, commit));
            }
        }
        return changes;
    }

    public byte[] getSourceCode() throws IOException {
        String command = "git --git-dir=" + commit.getBranch().getLocalPathDownloads() 
                          + "/.git --work-tree="
                + commit.getBranch().getLocalPathDownloads() + " show "
                + commit.getHash() + ":" + fileName;
        Log.writeLog("Exec git command to get source code in file " + fileName);
        Process powerShellProcess = Runtime.getRuntime().exec(command);
        powerShellProcess.getOutputStream().close();
        String line;
        BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));
        StringBuilder sb = new StringBuilder("");
        while ((line = stdout.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString().getBytes();
    }
    
    public String getLocalPath() {
        return commit.getLocalPathCommits()+"/"+fileName;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FileModificationTypeEnum getModificationType() {
        return modificationType;
    }

    public void setModificationType(FileModificationTypeEnum modificationType) {
        this.modificationType = modificationType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Class getJavaClass() {
        return javaClass;
    }

    public void setJavaClass(Class javaClass) {
        this.javaClass = javaClass;
    }

    public List<ClassCommitChange> getClassCommitchange() {
		return classCommitchange;
	}

	public void setClassCommitchange(List<ClassCommitChange> classCommitchange) {
		this.classCommitchange = classCommitchange;
	}

	public Integer getTotalLines() {
        return totalLines;
    }

    public void setTotalLines(Integer totalLines) {
        this.totalLines = totalLines;
    }       

    @Override
    public String toString() {
        return "CommitChange{" + "id=" + id + ", modificationType=" + modificationType + ", fileName=" + fileName + ", commit=" + commit.getHash() + '}';
    }
}


