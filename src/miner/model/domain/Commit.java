package miner.model.domain;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import miner.util.Log;

import miner.util.exception.ValidationException;

public class Commit {

    private String hash;

    private Date date;

    private String author;

    private String emailAuthor;

    private String subject;

    private Branch branch;

    private List<CommitChange> changes;

    public Commit() {

    }

    public Commit(String hash, Date date, String author, String emailAuthor, Branch branch, String subject) {
        super();
        this.hash = hash;
        this.date = date;
        this.author = author;
        this.emailAuthor = emailAuthor;
        this.branch = branch;
        this.subject = subject;
    }

    public String getLocalPathCommits() {
        return this.branch.getLocalPathCommits() + "/" + hash;
    }

    public static List<Commit> getCommits(Branch branch) throws Exception {
        String command[] = {"/bin/sh", "-c",
            "git --git-dir=" + branch.getLocalPathDownloads() + "/.git --work-tree="
            + branch.getLocalPathDownloads()
            + " log --reverse --pretty=format:%H###%h###%cn###%ce###%ct###%s " + branch.getName()};
        Log.writeLog("Exec git command to get all commits " + Arrays.toString(command));
        Process powerShellProcess = Runtime.getRuntime().exec(command);
        powerShellProcess.getOutputStream().close();
        List<Commit> commits = new ArrayList<>();
        String line;
        BufferedReader stdout = new BufferedReader(new InputStreamReader(powerShellProcess.getInputStream()));
        while ((line = stdout.readLine()) != null) {
            Log.writeLog("Return " + line);
            String[] values = line.split("###");
            String hash = values[0];
            String author = values[2];
            String email = values[3];
            Date date = new Date(Long.parseLong(values[4]) * 1000);
            String subject = values.length > 5 ? values[5] : "";
            commits.add(new Commit(hash, date, author, email, branch, subject));
        }
        if (commits.isEmpty()) {
            Log.writeLog("Cant found commits in branch " + branch.getName());
            throw new ValidationException("Cant found commits in branch " + branch.getName());
        }
        return commits;
    }

    public String getLocalPath() {
        return getBranch().getLocalPathCommits() + "/" + getHash();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEmailAuthor() {
        return emailAuthor;
    }

    public void setEmailAuthor(String emailAuthor) {
        this.emailAuthor = emailAuthor;
    }

    public List<CommitChange> getChanges() {
        return changes;
    }

    public void setChanges(List<CommitChange> changes) {
        this.changes = changes;
    }

}
