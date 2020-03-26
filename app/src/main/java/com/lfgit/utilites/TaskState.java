package com.lfgit.utilites;

import com.lfgit.utilites.Constants.Task;
import com.lfgit.utilites.Constants.InnerState;

public class TaskState {
    private InnerState mInnerState;
    private Task mPendingTask;

    public TaskState(InnerState state, Task pendingTask) {
        mInnerState = state;
        mPendingTask = pendingTask;
    }
    public void newState(InnerState state, Task pendingTask) {
        mInnerState = state;
        mPendingTask = pendingTask;
    }
    public InnerState getInnerState() {
        return mInnerState;
    }
    public void setInnerState(InnerState mInnerState) {
        this.mInnerState = mInnerState;
    }
    public Task getPendingTask() {
        return mPendingTask;
    }
    public void setPendingTask(Task mPendingTask) {
        this.mPendingTask = mPendingTask;
    }
}
