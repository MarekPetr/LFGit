package com.lfgit.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.databinding.DataBindingUtil;
import com.lfgit.databinding.ActivityInitRepoBinding;

import com.lfgit.R;
import com.lfgit.utilites.UriHelper;
import com.lfgit.view_models.AddRepoViewModel;

/**
 * Init/Clone activity
 */
public class AddRepoActivity extends BasicAbstractActivity {
    private ActivityInitRepoBinding mBinding;
    private AddRepoViewModel mAddRepoViewModel;
    private static final int INIT_BROWSE_REQUEST_CODE = 1;
    private static final int CLONE_BROWSE_REQUEST_CODE = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_init_repo);
        mAddRepoViewModel= new ViewModelProvider(this).get(AddRepoViewModel.class);
        mBinding.setAddRepoViewModel(mAddRepoViewModel);
        mBinding.setLifecycleOwner(this);

        mAddRepoViewModel.getExecResult().observe(this, mAddRepoViewModel::processExecResult);

        mAddRepoViewModel.getAllRepos().observe(this, repoList -> {
            mAddRepoViewModel.setRepos(repoList);
        });

        mAddRepoViewModel.getCloneResult().observe(this, cloneResult -> {
            showToastMsg(cloneResult);
            finish();
        });

        mAddRepoViewModel.getInitResult().observe(this, initResult -> {
            showToastMsg(initResult);
            finish();
        });

        mAddRepoViewModel.getExecPending().observe(this, isPending -> {
            if (isPending) showProgressDialog();
            else hideProgressDialog();
        });

        mAddRepoViewModel.getShowToast().observe(this, this::showToastMsg);

    }

    public void cloneBrowseButtonHandler(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, CLONE_BROWSE_REQUEST_CODE);
    }

    public void initBrowseButtonHandler(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, INIT_BROWSE_REQUEST_CODE);
    }

    public String setRepoPath(Intent intent, int requestCode) {
        Uri uri = intent.getData();
        String path = UriHelper.getDirPath(this, uri);
        // path is null when primary directory URI was not returned from intent
        if (path != null) {
            if (requestCode == INIT_BROWSE_REQUEST_CODE) {
                mBinding.getAddRepoViewModel().setInitRepoPath(path);
            } else if (requestCode == CLONE_BROWSE_REQUEST_CODE) {
                mBinding.getAddRepoViewModel().setCloneRepoPath(path);
            }
        } else {
            showToastMsg(getString (R. string.internal_storage_only));
        }
        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            String path = setRepoPath(intent, requestCode);
            if (requestCode == INIT_BROWSE_REQUEST_CODE) {            ;
                mBinding.initPathEditText.setText(path);
            }
            else if (requestCode == CLONE_BROWSE_REQUEST_CODE) {
                mBinding.cloneLocalPathEditText.setText(path);
            }
        }
    }
}
