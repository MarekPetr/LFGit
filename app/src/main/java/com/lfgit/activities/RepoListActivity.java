package com.lfgit.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.lfgit.BuildConfig;
import com.lfgit.R;
import com.lfgit.adapters.RepoListAdapter;
import com.lfgit.databinding.ActivityRepoListBinding;
import com.lfgit.interfaces.TaskListener;
import com.lfgit.installer.AssetInstaller;
import com.lfgit.view_models.RepoListViewModel;
import com.lfgit.view_models.LocalRepoViewModel;

public class RepoListActivity extends BasicAbstractActivity implements TaskListener {

    String TAG = "petr";
    ProgressDialog mProgressDialog;
    private ActivityRepoListBinding mBinding;
    private RepoListAdapter mRepoListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkAndRequestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (isFirstRun()) {
            AssetInstaller installer = new AssetInstaller(getAssets(), this);
            installer.execute(true);
        }
        RepoListViewModel repoListViewModel = ViewModelProviders.of(this).get(RepoListViewModel.class);
        LocalRepoViewModel localRepoViewModel = ViewModelProviders.of(this).get(LocalRepoViewModel.class);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_repo_list);
        mBinding.setLifecycleOwner(this);
        mBinding.setRepoListViewModel(repoListViewModel);
        mBinding.setLocalRepoViewModel(localRepoViewModel);
        mRepoListAdapter = new RepoListAdapter(this);
        mRepoListAdapter.addAllRepos();
        mBinding.repoList.setAdapter(mRepoListAdapter);
        mBinding.repoList.setOnItemClickListener(mRepoListAdapter);
        mBinding.repoList.setOnItemLongClickListener(mRepoListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_repo_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.initRepo:
                showToastMsg("initializing repo");

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Boolean isFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            // This is just a normal run
            return false;
        } else if (savedVersionCode == DOESNT_EXIST) {
            // This is a new install (or the user cleared the shared preferences)
        } else if (currentVersionCode > savedVersionCode) {
            // This is an upgrade
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
        return true;
    }

    @Override
    public void onTaskStarted() {
        lockScreenOrientation();
        mProgressDialog = ProgressDialog.show(this, "Installing...", "Getting things ready..");
    }

    @Override
    public void onTaskFinished() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            unlockScreenOrientation();
        }
    }
}

