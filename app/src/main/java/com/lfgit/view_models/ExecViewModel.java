package com.lfgit.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lfgit.executors.ExecListener;
import com.lfgit.executors.GitExec;
import com.lfgit.utilites.Constants;

public abstract class ExecViewModel extends AndroidViewModel implements ExecListener {
    GitExec mGitExec;
    MutableLiveData<Boolean> mExecPending = new MutableLiveData<>();

    ExecViewModel(@NonNull Application application) {
        super(application);
        mGitExec = new GitExec(this);
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
