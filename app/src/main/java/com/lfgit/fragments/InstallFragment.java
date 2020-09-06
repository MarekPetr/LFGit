package com.lfgit.fragments;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.lfgit.R;
import com.lfgit.activities.RepoListActivity;
import com.lfgit.install.AsyncTaskListener;
import com.lfgit.install.InstallTask;
import com.lfgit.view_models.RepoListViewModel;
import com.lfgit.view_models.RepoTasksViewModel;

import org.jetbrains.annotations.NotNull;

/**
 * Install packages.
 * Handle activity lifecycle.
 *
 * source:
 * https://androidresearch.wordpress.com/2013/05/10/dealing-with-asynctask-and-screen-orientation/
 */

public class InstallFragment extends Fragment implements AsyncTaskListener {
    private ProgressDialog mProgressDialog;
    private boolean isTaskRunning = false;
    private boolean isFirstRun = true;
    private RepoListActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // If we are returning here from a screen orientation
        // and the AsyncTask is still working, re-create and display the
        // progress dialog.

        if (isTaskRunning) {
            showProgressDialog();
        }
        if (isFirstRun) {
            InstallTask installer  = new InstallTask(this);
            installer.execute(true);
            isFirstRun = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_repo_list, container, false);
    }

    @Override
    public void onTaskStarted() {
        isTaskRunning = true;
        showProgressDialog();
    }

    @Override
    public void onTaskFinished(Boolean success) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        isTaskRunning = false;
        mActivity.onPackagesInstalled(success);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mActivity = (RepoListActivity) context;
    }

    @Override
    public void onDetach() {
        // All dialogs should be closed before leaving the activity in order to avoid
        // the: Activity has leaked window com.android.internal.policy... exception
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mActivity = null;
        super.onDetach();
    }
    private void showProgressDialog() {
        String title = mActivity.getResources().getString(R.string.install_progress_title);
        String msg = mActivity.getResources().getString(R.string.install_progress_msg);
        mProgressDialog = ProgressDialog.show(getActivity(), title, msg);
    }
}