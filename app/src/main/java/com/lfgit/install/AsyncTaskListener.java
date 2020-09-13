package com.lfgit.install;

import com.lfgit.utilites.ErrorWrapper;

public interface AsyncTaskListener {
    void onTaskStarted();

    void onTaskFinished(ErrorWrapper retWrapper);
}