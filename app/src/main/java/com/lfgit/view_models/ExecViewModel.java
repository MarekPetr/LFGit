package com.lfgit.view_models;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lfgit.database.RepoRepository;
import com.lfgit.executors.ExecListener;
import com.lfgit.executors.GitExec;
import com.lfgit.utilites.Constants;
import com.lfgit.utilites.TaskState;
import com.lfgit.view_models.Events.SingleLiveEvent;

import static com.lfgit.utilites.Constants.InnerState.*;
import static com.lfgit.utilites.Constants.PendingTask.*;

/**
 * A common ViewModel for other ViewModels handling binary files execution
 * */
public abstract class ExecViewModel extends AndroidViewModel implements ExecListener {

    /** Result wrapper */
    public static class ExecResult {
        private String result;
        private int errCode;

        public ExecResult(String result, int errCode) {
            this.result = result;
            this.errCode = errCode;
        }
        public String getResult() {
            return result;
        }
        public void setResult(String result) {
            this.result = result;
        }
        public int getErrCode() {
            return errCode;
        }
        public void setErrCode(int errCode) {
            this.errCode = errCode;
        }
    }

    Application mApplication;
    GitExec mGitExec;
    RepoRepository mRepository;
    public TaskState mState = new TaskState(FOR_APP, NONE);

    private MutableLiveData<ExecResult> mExecResult = new MutableLiveData<>();
    private SingleLiveEvent<String> mShowToast = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> mExecPending = new SingleLiveEvent<>();

    ExecViewModel(@NonNull Application application) {
        super(application);
        mApplication = application;
        mRepository = new RepoRepository(application);
        mGitExec = new GitExec(this);
    }

    String getAppString(int resId) {
        return mApplication.getString(resId);
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
        postExecResult(new ExecResult(result, errCode));
    }

    /** Check if task is long running */
    Boolean isLongTask(Constants.PendingTask pendingTask) {
        return pendingTask == PUSH || pendingTask == PULL || pendingTask == CLONE ||
                pendingTask == SHALLOW_CLONE ||  pendingTask == CHECKOUT_REMOTE ||
                pendingTask == CHECKOUT_LOCAL;
    }

    /** Check if long running task is finished */
    Boolean longUserTaskFinished(TaskState state) {
        Constants.PendingTask currentPendingTask = state.getPendingTask();
        return (state.getInnerState() == FOR_USER && isLongTask(currentPendingTask));
    }
    
    // background thread
    void showPendingIfNeeded(TaskState state) {
        if (longUserTaskFinished(state)) postShowPending();
    }

    // background thread
    void hidePendingIfNeeded(TaskState state) {
        if (longUserTaskFinished(state)) postHidePending();
    }

    public SingleLiveEvent<Boolean> getExecPending() {
        return mExecPending;
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

    public MutableLiveData<ExecResult> getExecResult() {
        return mExecResult;
    }
    public void postExecResult(ExecResult execResult) {
        mExecResult.postValue(execResult);
    }
}
