package com.lfgit.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.lfgit.database.model.Repo;

import java.util.List;

public class RepoRepository {
    private RepoDao mRepoDao;
    private LiveData<List<Repo>> mAllRepos;

    public RepoRepository(Context context) {
        RepoDatabase db = RepoDatabase.getInstance(context);
        mRepoDao = db.repoDao();
        mAllRepos = mRepoDao.getAllRepos();
    }

    public void insertRepo(final Repo repo) {
        RepoDatabase.databaseWriteExecutor.execute(() -> mRepoDao.insertRepo(repo));
    }

    public void insertList(final List<Repo> repos) {
        RepoDatabase.databaseWriteExecutor.execute(() -> mRepoDao.insertList(repos));
    }

    LiveData<List<Repo>> getAllRepos() {
        return mAllRepos;
    }

    /*public void getAllRepos() {
        RepoDatabase.databaseWriteExecutor.execute(() -> mRepoDao.getAllRepos());
    }*/
}
