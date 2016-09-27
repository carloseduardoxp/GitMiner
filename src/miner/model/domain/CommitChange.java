package miner.model.domain;

import java.util.List;

import org.eclipse.jgit.diff.DiffEntry.ChangeType;

public class CommitChange {

    private Integer id;

    private ChangeType changeType;    

    private String oldFileName;
    
    private String newFileName;

    private Commit commit;    
    
    private byte[] sourceCode;
    
    private List<ClassCommitChange> classCommitchange;

    public CommitChange() {
    }
    
    public CommitChange(Integer id) {
        this.id = id;
    }

    public CommitChange(ChangeType changeType, String oldFileName, String newFileName, Commit commit) {
        this(changeType,oldFileName,newFileName,null,commit);
    }
    
    public CommitChange(ChangeType changeType, String oldFileName, String newFileName, byte[] sourceCode,Commit commit) {
        super();
        this.sourceCode = sourceCode;
        this.changeType = changeType;
        this.oldFileName = oldFileName;
        this.newFileName = newFileName;
        this.commit = commit;
    }
        
    public String getLocalPath() {
        return commit.getLocalPathCommits()+"/"+newFileName;
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

    public List<ClassCommitChange> getClassCommitchange() {
		return classCommitchange;
	}

	public void setClassCommitchange(List<ClassCommitChange> classCommitchange) {
		this.classCommitchange = classCommitchange;
	}

    public ChangeType getChangeType() {
		return changeType;
	}

	public void setChangeType(ChangeType changeType) {
		this.changeType = changeType;
	}

	public String getOldFileName() {
		return oldFileName;
	}

	public void setOldFileName(String oldFileName) {
		this.oldFileName = oldFileName;
	}

	public String getNewFileName() {
		return newFileName;
	}

	public void setNewFileName(String newFileName) {
		this.newFileName = newFileName;
	}

	public byte[] getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(byte[] sourceCode) {
		this.sourceCode = sourceCode;
	}

	@Override
	public String toString() {
		return "CommitChange [id=" + id + ", changeType=" + changeType + ", oldFileName=" + oldFileName
				+ ", newFileName=" + newFileName + ", commit=" + commit + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		CommitChange other = (CommitChange) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
	
}


