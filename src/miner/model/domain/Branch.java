package miner.model.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import miner.util.Log;
import miner.util.exception.ObjectNotFoundException;
import miner.util.exception.ValidationException;

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
        return this.project.getLocalPathDownload()+ "/" + name;
    }
    
    public String getLocalPathClasses() {
        return this.project.getLocalPathClasses()+ "/" + name;
    }

    public String getLocalPathCommits() {
        return this.project.getLocalPathCommits()+ "/" + name;
    }
    

    public boolean isAlreadyDownloaded() {
        File file = new File(getLocalPathDownloads());
        return file.isDirectory() && file.exists();
    }

    public void downloadBranch() throws IOException,ObjectNotFoundException,ValidationException {
        String command = "git clone -b " + name + " " + project.getUrl() + " " + getLocalPathDownloads();
        Log.writeLog("Exec git command to download branch local "+command);
        Process powerShellProcess = Runtime.getRuntime().exec(command);
        powerShellProcess.getOutputStream().close();
        String line;
        BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));
        while ((line = stdout.readLine()) != null) {
            Log.writeLog("Return "+line);
        }
    }

    public static List<Branch> getBranchesProject(Project project) throws ObjectNotFoundException,
            ValidationException,IOException {
        String command = "git ls-remote --heads " + project.getUrl();
        Log.writeLog("Exec git command to get all branch names "+command);
        List<Branch> branches = new ArrayList<>();
        Process powerShellProcess = Runtime.getRuntime().exec(command);
        powerShellProcess.getOutputStream().close();
        String line;
        BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));
        while ((line = stdout.readLine()) != null) {
            String result[] = line.split("	");
            Log.writeLog("Return "+Arrays.toString(result));
            String branch = result[1].replace("refs/heads/", "");
            branches.add(new Branch(branch, project));
        }
        return branches;
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
