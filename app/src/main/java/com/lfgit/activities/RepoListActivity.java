package com.lfgit.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.lfgit.BuildConfig;
import com.lfgit.R;
import com.lfgit.adapters.RepoListAdapter;
import com.lfgit.databinding.ActivityRepoListBinding;
import com.lfgit.interfaces.AsyncTaskListener;
import com.lfgit.utilites.AssetInstaller;
import com.lfgit.view_models.RepoListViewModel;

public class RepoListActivity extends BasicAbstractActivity implements AsyncTaskListener {

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
        RepoListViewModel repoListViewModel = new ViewModelProvider(this).get(RepoListViewModel.class);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_repo_list);
        mBinding.setLifecycleOwner(this);
        mBinding.setRepoListViewModel(repoListViewModel);

        mRepoListAdapter = new RepoListAdapter(this, repoListViewModel);
        mBinding.repoList.setAdapter(mRepoListAdapter);
        mBinding.repoList.setOnItemClickListener(mRepoListAdapter);
        mBinding.repoList.setOnItemLongClickListener(mRepoListAdapter);

        repoListViewModel.getAllRepos().observe(this, repoList ->
                mRepoListAdapter.setRepos(repoList)
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_repo_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        Class intent_class;
        switch(item.getItemId()) {
            case R.id.menu_init_repo:
                intent_class = InitRepoActivity.class;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        intent = new Intent(this, intent_class);
        this.startActivity(intent);
        return true;
    }

    private Boolean isFirstRun() {

        final String PREFS_NAME = "FirstRunPref";
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

