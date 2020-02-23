package com.lfgit.activities;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProviders;
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
        LocalRepoViewModel localRepoViewModel = ViewModelProviders.of(this).get(LocalRepoViewModel.class);
        mBinding.setLocalRepoViewModel(localRepoViewModel);
        mBinding.setLifecycleOwner(this);
    }

    public void initButtonHandler(View view) {
        mBinding.getLocalRepoViewModel().initLocalRepo();
    }
}
