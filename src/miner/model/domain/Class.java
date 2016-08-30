package miner.model.domain;

import java.util.List;
import java.util.Set;

public class Class {

    private Integer id;

    private String name;

    private Branch branch;

    private List<ClassCommitChange> changes;

    private Set<SmellEnum> smells;

    public Class() {

    }
    
    public Class(Integer id) {
    	this.id = id;
    }

    public Class(String name, Branch branch) {
        this.name = name;
        this.branch = branch;
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

    
    
    

}
