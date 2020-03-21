package com.lfgit.executors;

import com.lfgit.utilites.Constants;

public interface ExecCallback {
    void passResult(String result);
    void passErrCode(int errCode, String task);
}
