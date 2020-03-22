package com.lfgit.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lfgit.database.RepoRepository;
import com.lfgit.database.model.Repo;
import com.lfgit.executors.ExecListener;
import com.lfgit.executors.GitExec;
import com.lfgit.utilites.Constants;

import java.util.List;

public abstract class ExecViewModel extends AndroidViewModel implements ExecListener {
    GitExec mGitExec;
    RepoRepository mRepository;
    List<Repo> mAllRepos;
    MutableLiveData<Boolean> mExecPending = new MutableLiveData<>();

    ExecViewModel(@NonNull Application application) {
        super(application);
        mRepository = new RepoRepository(application);
        mGitExec = new GitExec(this);
    }

    public void setAllRepos(List<Repo> repoList) {
        mAllRepos = repoList;
    }

    @Override
    public void onExecStarted() {
        setPending();
    }

    @Override
    public void onExecFinished(Constants.RepoTask task, String result, int errCode) {
        unsetPending();
    }

    void setPending() {
        mExecPending.postValue(true);
    }

    void unsetPending() {
        mExecPending.postValue(false);
    }

    public MutableLiveData<Boolean> getExecPending() {
        return mExecPending;
    }

}
