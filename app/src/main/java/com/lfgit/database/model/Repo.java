package com.lfgit.database.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// TODO https://medium.com/mindorks/using-room-database-android-jetpack-675a89a0e942
// DAO

@Entity(tableName = "repo")
public class Repo {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "localPath")
    private String localPath;
    @ColumnInfo(name = "remoteURL")
    private String remoteURL;
    @ColumnInfo(name = "repoStatus")
    private String repoStatus;
    @ColumnInfo(name = "username")
    private String username;
    @ColumnInfo(name = "password")
    private String password;
    @ColumnInfo(name = "latestCommitterUname")
    private String latestCommitterUname;
    @ColumnInfo(name = "latestCommitterEmail")
    private String latestCommitterEmail;
    @ColumnInfo(name = "latestCommitDate")
    private String latestCommitDate;
    @ColumnInfo(name = "latestCommitMsg")
    private String latestCommitMsg;

    public Repo(String localPath) {
        this.localPath = localPath;
        this.remoteURL = remoteURL;
        this.repoStatus = repoStatus;
    }
    public String getDisplayName() {
        return localPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getRemoteURL() {
        return remoteURL;
    }

    public void setRemoteURL(String remoteURL) {
        this.remoteURL = remoteURL;
    }

    public String getRepoStatus() {
        return repoStatus;
    }

    public void setRepoStatus(String repoStatus) {
        this.repoStatus = repoStatus;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLatestCommitterUname() {
        return latestCommitterUname;
    }

    public void setLatestCommitterUname(String latestCommitterUname) {
        this.latestCommitterUname = latestCommitterUname;
    }

    public String getLatestCommitterEmail() {
        return latestCommitterEmail;
    }

    public void setLatestCommitterEmail(String latestCommitterEmail) {
        this.latestCommitterEmail = latestCommitterEmail;
    }

    public String getLatestCommitDate() {
        return latestCommitDate;
    }

    public void setLatestCommitDate(String latestCommitDate) {
        this.latestCommitDate = latestCommitDate;
    }

    public String getLatestCommitMsg() {
        return latestCommitMsg;
    }

    public void setLatestCommitMsg(String latestCommitMsg) {
        this.latestCommitMsg = latestCommitMsg;
    }
}