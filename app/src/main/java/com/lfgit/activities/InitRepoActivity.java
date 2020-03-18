package com.lfgit.activities;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.databinding.DataBindingUtil;
import com.lfgit.databinding.ActivityInitRepoBinding;

import com.lfgit.R;
import com.lfgit.view_models.LocalRepoViewModel;

public class InitRepoActivity extends BasicAbstractActivity {
    private ActivityInitRepoBinding mBinding;

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
            String repoPath = mBinding.getLocalRepoViewModel().getRepoName();
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
}
