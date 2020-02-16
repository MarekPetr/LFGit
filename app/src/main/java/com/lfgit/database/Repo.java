package com.lfgit.database;

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

}