package com.lfgit.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.lfgit.database.RepoRepository;
import com.lfgit.executors.ExecListener;
import com.lfgit.executors.GitExec;
import com.lfgit.utilites.Constants;
import com.lfgit.utilites.TaskState;
import com.lfgit.view_models.Events.SingleLiveEvent;

import static com.lfgit.utilites.Constants.InnerState.*;
import static com.lfgit.utilites.Constants.Task.*;

public abstract class ExecViewModel extends AndroidViewModel implements ExecListener {
    GitExec mGitExec;
    RepoRepository mRepository;

    TaskState mState = new TaskState(FOR_APP, NONE);
    private SingleLiveEvent<String> mShowToast = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> mExecPending = new SingleLiveEvent<>();

    ExecViewModel(@NonNull Application application) {
        super(application);
        mRepository = new RepoRepository(application);
        mGitExec = new GitExec(this);
    }

    // background thread
    @Override
    public void onExecStarted() {
        showPendingIfNeeded(mState);
    }

    // background thread
    @Override
    public void onExecFinished(String result, int errCode) {
        hidePendingIfNeeded(mState);
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
    Boolean isLongTask(Constants.Task task) {
        return task == PUSH || task == PULL || task == CLONE ||
                task == CHECKOUT_REMOTE || task == CHECKOUT_LOCAL;
    }
    
    // background thread
    Boolean longTaskFinished(TaskState state) {
        Constants.Task currentTask = state.getPendingTask();
        if (isLongTask(currentTask)) {
            return state.getInnerState() == FOR_USER;
        }
        return false;
    }
    
    // background thread
    void showPendingIfNeeded(TaskState state) {
        if (longTaskFinished(state)) postShowPending();
    }

    // background thread
    // hides pending when task state is FINISH
    void hidePendingIfNeeded(TaskState state) {
        if (longTaskFinished(state)) postHidePending();
    }

    public SingleLiveEvent<Boolean> getExecPending() {
        return mExecPending;
    }
}
