package com.lfgit.activities;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.databinding.DataBindingUtil;
import com.lfgit.databinding.ActivityInitRepoBinding;

import com.lfgit.R;
import com.lfgit.view_models.InitRepoViewModel;

public class InitRepoActivity extends BasicAbstractActivity {
    private ActivityInitRepoBinding mBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_init_repo);
        InitRepoViewModel initRepoViewModel = new ViewModelProvider(this).get(InitRepoViewModel.class);
        mBinding.setInitRepoViewModel(initRepoViewModel);
        mBinding.setLifecycleOwner(this);
    }

    public void initButtonHandler(View view) {
        if (mBinding.getInitRepoViewModel().initLocalRepo()) {
            String repoPath = mBinding.getInitRepoViewModel().getRepoName();
            showToastMsg("New git repository \"" + repoPath + "\" initialized");
            finish();
        } else {
            showToastMsg("Please enter the repo directory");
        }

    }
}
