package com.lfgit.executors;

import com.lfgit.utilites.Constants;

public interface ExecListener {
    void onExecStarted();
    void onExecFinished(Constants.RepoTask task, String result, int errCode);
}
