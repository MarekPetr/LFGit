package com.lfgit.executors;

public interface ExecListener {
    void onExecStarted();
    void onExecFinished(String result, int errCode);
}
