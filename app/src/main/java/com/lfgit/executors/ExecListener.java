package com.lfgit.executors;

/**
 * Implement to invoke methods during execution.
 */
public interface ExecListener {
    void onExecStarted();
    void onExecFinished(String result, int errCode);
}
