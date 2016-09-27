package miner.model.domain;

import java.io.File;
import java.util.List;

public class Branch {

    private Integer id;

    private String name;

    private Project project;

    private List<Commit> commits;

    private List<Class> classes;

    public Branch() {

    }

    public Branch(String name, Project project) {
        super();
        this.name = name;
        this.project = project;
    }
    
    public String getLocalPathDownloads() {
        return (this.project.getLocalPathDownload()+ "/" + name).replaceAll("\\\\","/").replaceAll("//","/");
    }
    
    public String getGitDir() {
        return (this.getLocalPathDownloads()+"/.git").replaceAll("//","/");
    }

    
    public String getLocalPathClasses() {
        return (this.project.getLocalPathClasses()+ "/" + name).replaceAll("\\\\","/").replaceAll("//","/");
    }

    public String getLocalPathCommits() {
        return (this.project.getLocalPathCommits()+ "/" + name).replaceAll("\\\\","/").replaceAll("//","/");
    }
    

    public boolean isAlreadyDownloaded() {
        File file = new File(getLocalPathDownloads());
        return file.isDirectory() && file.exists();
    }
    
    public Boolean isBranchMaster() {
        return name.equals("master") || name.equals("trunk");
    }

    public List<Commit> getCommits() {
        return commits;
    }

    public void setCommits(List<Commit> commits) {
        this.commits = commits;
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Class> getClasses() {
        return classes;
    }

    public void setClasses(List<Class> classes) {
        this.classes = classes;
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
		Branch other = (Branch) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
    
    

}
