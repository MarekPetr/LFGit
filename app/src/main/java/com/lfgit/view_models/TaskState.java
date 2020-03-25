package com.lfgit.view_models;

import com.lfgit.utilites.Constants;
import com.lfgit.utilites.Constants.task;
import com.lfgit.utilites.Constants.innerState;

class TaskState {
    private Constants.innerState mInnerState;
    private Constants.task mPendingTask;

    TaskState(innerState state, task pendingTask) {
        mInnerState = state;
        mPendingTask = pendingTask;
    }
    void newState(innerState state, task pendingTask) {
        mInnerState = state;
        mPendingTask = pendingTask;
    }
    innerState getTaskState() {
        return mInnerState;
    }
    void setTaskState(innerState mInnerState) {
        this.mInnerState = mInnerState;
    }
    task getPendingTask() {
        return mPendingTask;
    }
    void setPendingTask(task mPendingTask) {
        this.mPendingTask = mPendingTask;
    }
}
