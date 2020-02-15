package com.lfgit.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;

import com.lfgit.BuildConfig;
import com.lfgit.R;
import com.lfgit.adapters.RepoListAdapter;
import com.lfgit.databinding.RepoListBinding;
import com.lfgit.interfaces.TaskListener;
import com.lfgit.adapters.RepoOperationsAdapter;
import com.lfgit.importer.AssetImporter;
import com.lfgit.view_models.RepoListViewModel;

public class RepoListActivity extends BasicAbstractActivity implements TaskListener {

    String TAG = "petr";
    ProgressDialog mProgressDialog;
    private RelativeLayout mRightDrawer;
    private DrawerLayout mDrawerLayout;
    private ListView mRepoOperationList;
    private RepoOperationsAdapter mDrawerAdapter;
    private RepoListBinding mBinding;
    private RepoListAdapter mRepoListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_list);

        setupDrawer();
        checkAndRequestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (isFirstRun()) {
            AssetImporter importer = new AssetImporter(getAssets(), this);
            importer.execute(true);
        }
        RepoListViewModel viewModel = ViewModelProviders.of(this).get(RepoListViewModel.class);

        mBinding = DataBindingUtil.setContentView(this, R.layout.repo_list);
        mBinding.setLifecycleOwner(this);
        mBinding.setViewModel(viewModel);
        mRepoListAdapter = new RepoListAdapter(this);
        mRepoListAdapter.addAllRepos();
        mBinding.repoList.setAdapter(mRepoListAdapter);
        mBinding.repoList.setOnItemClickListener(mRepoListAdapter);
        mBinding.repoList.setOnItemLongClickListener(mRepoListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toggle_drawer) {
            if (mDrawerLayout.isDrawerOpen(mRightDrawer)) {
                mDrawerLayout.closeDrawer(mRightDrawer);
            } else {
                mDrawerLayout.openDrawer(mRightDrawer);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mRightDrawer = findViewById(R.id.right_drawer);

        mRepoOperationList = findViewById(R.id.repoOperationList);
        mDrawerAdapter = new RepoOperationsAdapter(this);
        mRepoOperationList.setAdapter(mDrawerAdapter);
        mRepoOperationList.setOnItemClickListener(mDrawerAdapter);
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

