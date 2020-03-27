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
import com.lfgit.view_models.Events.SingleLiveEvent;

import java.util.List;

import static com.lfgit.utilites.Constants.InnerState.FOR_USER;
import static com.lfgit.utilites.Constants.InnerState.FOR_APP;
import static com.lfgit.utilites.Constants.Task.CLONE;
import static com.lfgit.utilites.Constants.Task.NONE;
import static com.lfgit.utilites.Constants.Task.PULL;
import static com.lfgit.utilites.Constants.Task.PUSH;

public abstract class ExecViewModel extends AndroidViewModel implements ExecListener {
    GitExec mGitExec;
    RepoRepository mRepository;
    List<Repo> mAllRepos;
    TaskState mState = new TaskState(FOR_APP, NONE);
    SingleLiveEvent<String> mShowToast = new SingleLiveEvent<>();
    SingleLiveEvent<Boolean> mExecPending = new SingleLiveEvent<>();

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
    public void onExecStarted() {
        showPendingOnRemoteUserTask(mState);
    }

    // background thread
    @Override
    public void onExecFinished(String result, int errCode) {
        hidePendingOnRemoteUserTask(mState);
    }

    // background thread
    void postShowPending() {
        mExecPending.postValue(true);
    }

    // background thread
    void postHidePending() {
        mExecPending.postValue(false);
    }

    public SingleLiveEvent<String> getShowToast() {
        return mShowToast;
    }
    void setShowToast(String message) {
        mShowToast.setValue(message);
    }
    void postShowToast(String message) {
        mShowToast.postValue(message);
    }

    // background thread
    Boolean isRemoteTask(Constants.Task currentTask) {
        return currentTask == CLONE || currentTask == PUSH || currentTask == PULL;
    }
    
    // background thread
    Boolean remoteTaskFinished(TaskState state) {
        Constants.Task currentTask = state.getPendingTask();
        if (isRemoteTask(currentTask)) {
            return state.getInnerState() == FOR_USER;
        }
        return false;
    }
    
    // background thread
    void showPendingOnRemoteUserTask(TaskState state) {
        if (remoteTaskFinished(state)) postShowPending();
    }

    // background thread
    // hides pending when task state is FINISH
    void hidePendingOnRemoteUserTask(TaskState state) {
        if (remoteTaskFinished(state)) postHidePending();
    }

    public SingleLiveEvent<Boolean> getExecPending() {
        return mExecPending;
    }
}
