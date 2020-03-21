package com.lfgit.executors;

import com.lfgit.utilites.Constants;

public interface ExecCallback {
    void passResult(Constants.RepoTask task, String result, int errCode);
}
