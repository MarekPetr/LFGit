package com.lfgit.executors;
import com.lfgit.utilites.TaskState;

public interface ExecListener {
    void onExecStarted(TaskState state);
    void onExecFinished(TaskState state, String result, int errCode);
}
