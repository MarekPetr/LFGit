package com.lfgit.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.lfgit.R;
import com.lfgit.interfaces.AsyncTaskListener;
import com.lfgit.interfaces.FragmentCallback;
import com.lfgit.utilites.AssetInstaller;

import org.jetbrains.annotations.NotNull;
import java.util.Objects;

// source:
// https://androidresearch.wordpress.com/2013/05/10/dealing-with-asynctask-and-screen-orientation/
public class InstallFragment extends Fragment implements AsyncTaskListener {
    private ProgressDialog mProgressDialog;
    private boolean isTaskRunning = false;
    private boolean isFirstRun = true;
    private Context mContext;
    private FragmentCallback mCallback;

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
            AssetInstaller installer  = new AssetInstaller(mContext.getAssets(),this);
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
    public void onTaskFinished() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        isTaskRunning = false;
        Objects.requireNonNull(getActivity()).onBackPressed();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mContext=context;
    }

    @Override
    public void onDetach() {
        // All dialogs should be closed before leaving the activity in order to avoid
        // the: Activity has leaked window com.android.internal.policy... exception
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mCallback.fragmentDetached();
        super.onDetach();
    }
    private void showProgressDialog() {
        mProgressDialog = ProgressDialog.show(getActivity(), "Installing", "Please wait a moment!");
    }

    public void setCallback(FragmentCallback callback) {
        this.mCallback = callback;
    }
}