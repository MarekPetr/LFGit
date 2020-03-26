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
import com.lfgit.utilites.TaskState;

import java.util.List;

import static com.lfgit.utilites.Constants.InnerState.FINISH;
import static com.lfgit.utilites.Constants.Task.CLONE;
import static com.lfgit.utilites.Constants.Task.PULL;
import static com.lfgit.utilites.Constants.Task.PUSH;

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

    // background thread
    @Override
    public void onExecStarted(TaskState state) {
        postShowPendingOnRemoteFinish(state);   
    }

    // background thread
    @Override
    public void onExecFinished(TaskState state, String result, int errCode) {
        postHidePendingOnRemoteFinish(state);      
    }

    // background thread
    void postShowPending() {
        mExecPending.postValue(true);
    }

    // background thread
    void postHidePending() {
        mExecPending.postValue(false);
    }

    // background thread
    Boolean isRemoteTask(Constants.Task currentTask) {
        return currentTask == CLONE || currentTask == PUSH || currentTask == PULL;
    }
    
    // background thread
    Boolean remoteTaskFinished(TaskState state) {
        Constants.Task currentTask = state.getPendingTask();
        if (isRemoteTask(currentTask)) {
            return state.getInnerState() == FINISH;
        }
        return false;
    }
    
    // background thread
    void postShowPendingOnRemoteFinish(TaskState state) {
        if (remoteTaskFinished(state)) postShowPending();
    }

    // background thread
    void postHidePendingOnRemoteFinish(TaskState state) {
        if (remoteTaskFinished(state)) postHidePending();
    }

    public MutableLiveData<Boolean> getExecPending() {
        return mExecPending;
    }

}
