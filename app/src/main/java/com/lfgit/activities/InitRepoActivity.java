package com.lfgit.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.databinding.DataBindingUtil;
import com.lfgit.databinding.ActivityInitRepoBinding;

import com.lfgit.R;
import com.lfgit.utilites.UriHelper;
import com.lfgit.view_models.LocalRepoViewModel;

public class InitRepoActivity extends BasicAbstractActivity {
    private ActivityInitRepoBinding mBinding;
    private static final int INIT_BROWSE_REQUEST_CODE = 1;
    private static final int CLONE_BROWSE_REQUEST_CODE = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_init_repo);
        LocalRepoViewModel localRepoViewModel = new ViewModelProvider(this).get(LocalRepoViewModel.class);
        mBinding.setLocalRepoViewModel(localRepoViewModel);
        mBinding.setLifecycleOwner(this);
    }

    public void initButtonHandler(View view) {
        if (mBinding.getLocalRepoViewModel().initLocalRepo()) {
            String repoPath = mBinding.getLocalRepoViewModel().getRepoPath();
            showToastMsg("New git repository \"" + repoPath + "\" initialized");
            finish();
        } else {
            showToastMsg("Please enter the repo directory");
        }
    }

    public void cloneButtonHandler(View view) {
        if (mBinding.getLocalRepoViewModel().cloneRepo()) {
            showToastMsg("Git repo cloned");
            finish();
        } else {
            showToastMsg("Git clone failed");
        }
    }


    public void cloneBrowseButtonHandler(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, CLONE_BROWSE_REQUEST_CODE);
    }

    public void initBrowseButtonHandler(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, INIT_BROWSE_REQUEST_CODE);
    }

    public String setRepoPath(Intent intent) {
        Uri uri = intent.getData();
        String path = UriHelper.getDirPath(this, uri);
        if (path != null) {
            mBinding.getLocalRepoViewModel().setRepoPath(path);
        } else {
            showToastMsg(getString (R. string. browse_only_primary));
        }
        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == INIT_BROWSE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {            ;
            mBinding.initPathEditText.setText(setRepoPath(intent));
        }
        else if (requestCode == CLONE_BROWSE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mBinding.cloneLocalPathEditText.setText(setRepoPath(intent));
        }
    }
}
