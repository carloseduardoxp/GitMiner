package miner.model.domain;

import java.util.Date;
import java.util.List;

public class Commit {

    private String hash;
    
    private String authorName;

    private String authorEmail;

    private Date authorDate;

    private String committerName;
    
    private String committerEmail;
    
    private Date committerDate;

    private String fullMessage;
    
    private String shortMessage;

    private Branch branch;

    private List<CommitChange> changes;

    public Commit() {

    }

    public Commit(String hash, String authorName, String authorEmail, Date authorDate, String committerName,
			String committerEmail, Date committerDate, String fullMessage, String shortMessage, Branch branch) {
		super();
		this.hash = hash;
		this.authorName = authorName;
		this.authorEmail = authorEmail;
		this.authorDate = authorDate;
		this.committerName = committerName;
		this.committerEmail = committerEmail;
		this.committerDate = committerDate;
		this.fullMessage = fullMessage;
		this.shortMessage = shortMessage;
		this.branch = branch;
	}

	public String getLocalPathCommits() {
        return this.branch.getLocalPathCommits() + "/" + hash;
    }

    public String getLocalPath() {
        return getBranch().getLocalPathCommits() + "/" + getHash();
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public List<CommitChange> getChanges() {
        return changes;
    }

    public void setChanges(List<CommitChange> changes) {
        this.changes = changes;
    }

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getAuthorEmail() {
		return authorEmail;
	}

	public void setAuthorEmail(String authorEmail) {
		this.authorEmail = authorEmail;
	}

	public Date getAuthorDate() {
		return authorDate;
	}

	public void setAuthorDate(Date authorDate) {
		this.authorDate = authorDate;
	}

	public String getCommitterName() {
		return committerName;
	}

	public void setCommitterName(String committerName) {
		this.committerName = committerName;
	}

	public String getCommitterEmail() {
		return committerEmail;
	}

	public void setCommitterEmail(String committerEmail) {
		this.committerEmail = committerEmail;
	}

	public Date getCommitterDate() {
		return committerDate;
	}

	public void setCommitterDate(Date committerDate) {
		this.committerDate = committerDate;
	}

	public String getFullMessage() {
		return fullMessage;
	}

	public void setFullMessage(String fullMessage) {
		this.fullMessage = fullMessage;
	}

	public String getShortMessage() {
		return shortMessage;
	}

	public void setShortMessage(String shortMessage) {
		this.shortMessage = shortMessage;
	}

	@Override
	public String toString() {
		return "Commit [hash=" + hash + ", authorName=" + authorName + ", authorEmail=" + authorEmail + ", authorDate="
				+ authorDate + ", committerName=" + committerName + ", committerEmail=" + committerEmail
				+ ", committerDate=" + committerDate + ", fullMessage=" + fullMessage + ", shortMessage=" + shortMessage
				+ ", branch=" + branch + "]";
	}      
	
	

}
