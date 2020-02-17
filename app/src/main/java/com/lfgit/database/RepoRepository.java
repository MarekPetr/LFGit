package com.lfgit.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.lfgit.database.model.Repo;

import java.util.List;

public class RepoRepository {
    private RepoDao mRepoDao;
    private LiveData<List<Repo>> Repos;

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
