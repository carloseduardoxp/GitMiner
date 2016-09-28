package miner.model.domain;

import java.util.List;
import java.util.Set;

public class Class {

    private Integer id;

    private String name;
    
    private boolean analyse;

    private Branch branch;
    
    private boolean contentInterface;
    
    private boolean contentEnum;

    private List<ClassCommitChange> changes;

    private Set<SmellEnum> smells;

    public Class() {

    }
    
    public Class(Integer id,String name,Boolean analyse,boolean contentInterface,boolean contentEnum) {
    	this.id = id;
    	this.name = name;
    	this.analyse = analyse;
    	this.contentEnum = contentEnum;
    	this.contentInterface = contentInterface;
    }

    public Class(String name, Branch branch,Boolean analyse,boolean contentInterface,boolean contentEnum) {
        this.name = name;
        this.branch = branch;
        this.analyse = analyse;
    	this.contentEnum = contentEnum;
    	this.contentInterface = contentInterface;
    }        

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ClassCommitChange> getChanges() {
        return changes;
    }

    public void setChanges(List<ClassCommitChange> changes) {
        this.changes = changes;
    }

    public Set<SmellEnum> getSmells() {
        return smells;
    }

    public void setSmells(Set<SmellEnum> smells) {
        this.smells = smells;
    }


    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

	public boolean isAnalyse() {
		return analyse;
	}

	public void setAnalyse(boolean analyse) {
		this.analyse = analyse;
	}

	public boolean isContentInterface() {
		return contentInterface;
	}

	public void setContentInterface(boolean contentInterface) {
		this.contentInterface = contentInterface;
	}

	public boolean isContentEnum() {
		return contentEnum;
	}

	public void setContentEnum(boolean contentEnum) {
		this.contentEnum = contentEnum;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((branch == null) ? 0 : branch.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Class other = (Class) obj;
		if (branch == null) {
			if (other.branch != null)
				return false;
		} else if (!branch.equals(other.branch))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return id + "-" + name;
	}

    
    
    

}
