package com.lfgit.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.lfgit.database.model.Repo;

import java.util.List;

public class RepoRepository {
    private RepoDao mRepoDao;
    private LiveData<List<Repo>> mAllRepos;

    public RepoRepository(Application application) {
        RepoDatabase db = RepoDatabase.getInstance(application);
        mRepoDao = db.repoDao();
        mAllRepos = mRepoDao.getAllRepos();
    }

    public void insertRepo(Repo repo) {
        RepoDatabase.databaseWriteExecutor.execute(() -> mRepoDao.insertRepo(repo));
    }

    public void insertList(List<Repo> repos) {
        RepoDatabase.databaseWriteExecutor.execute(() -> mRepoDao.insertList(repos));
    }

    public LiveData<List<Repo>> getAllRepos() {
        return mAllRepos;
    }

    public void deleteByID(int repoId) {
        RepoDatabase.databaseWriteExecutor.execute(() -> mRepoDao.deleteByRepoId(repoId));
    }
}
