package com.lfgit.database;

import android.content.Context;

import com.lfgit.database.model.Repo;

import java.util.List;

public class RepoRepository {
    private RepoDao mRepoDao;

    public RepoRepository(Context context) {
        RepoDatabase db = RepoDatabase.getInstance(context);
        mRepoDao = db.repoDao();
    }

    public void insertRepo(final Repo repo) {
        new Thread(() -> mRepoDao.insertRepo(repo)).start();
    }

    public void insertList(final List<Repo> repos) {
        new Thread(() -> mRepoDao.insertList(repos)).start();
    }
}
