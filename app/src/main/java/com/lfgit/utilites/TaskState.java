package com.lfgit.utilites;

import com.lfgit.utilites.Constants.PendingTask;
import com.lfgit.utilites.Constants.InnerState;

public class TaskState {
    private InnerState mInnerState;
    private PendingTask mPendingTask;

    public TaskState(InnerState state, PendingTask pendingTask) {
        mInnerState = state;
        mPendingTask = pendingTask;
    }
    public void newState(InnerState state, PendingTask pendingTask) {
        mInnerState = state;
        mPendingTask = pendingTask;
    }
    public InnerState getInnerState() {
        return mInnerState;
    }
    public void setInnerState(InnerState mInnerState) {
        this.mInnerState = mInnerState;
    }
    public PendingTask getPendingTask() {
        return mPendingTask;
    }
    public void setPendingTask(PendingTask mPendingTask) {
        this.mPendingTask = mPendingTask;
    }
}
