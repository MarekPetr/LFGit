package com.lfgit.database;

public class Repo {
    private String mLocalPath;

    public Repo(String name) {
        mLocalPath = name;
    }

    public String getDisplayName() {
        return mLocalPath;
    }
}
