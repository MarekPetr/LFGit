package com.lfgit.database.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.lfgit.utilites.UriHelper;

import java.io.Serializable;

@Entity(tableName = "repo")
public class Repo implements Serializable {
    public static final String TAG = Repo.class.getSimpleName();
    private static final long serialVersionUID = -556977004352408504L;

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "localPath")
    private String localPath;
    @ColumnInfo(name = "remoteURL")
    private String remoteURL;
    @ColumnInfo(name = "username")
    private String username;
    @ColumnInfo(name = "password")
    private String password;

    public Repo(String localPath) {
        this.localPath = localPath;
        this.remoteURL = "";
    }

    @Ignore
    public Repo(String localPath, String remoteURL) {
        this.localPath = localPath;
        this.remoteURL = remoteURL;
    }

    public String getDisplayName() {
        return UriHelper.getGitDir(localPath);
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
}