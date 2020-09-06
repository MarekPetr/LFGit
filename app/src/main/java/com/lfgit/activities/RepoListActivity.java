package com.lfgit.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lfgit.BuildConfig;
import com.lfgit.R;
import com.lfgit.adapters.RepoListAdapter;
import com.lfgit.databinding.ActivityRepoListBinding;
import com.lfgit.fragments.InstallFragment;
import com.lfgit.utilites.Constants;
import com.lfgit.utilites.UriHelper;
import com.lfgit.view_models.RepoListViewModel;

/**
 * An activity implementing list of repositories and initial installation.
 */
public class RepoListActivity extends BasicAbstractActivity {
    private RepoListViewModel mRepoListViewModel;
    private RepoListAdapter mRepoListAdapter;
    private SwipeRefreshLayout pullToRefresh;
    private InstallPreference mInstallPref = new InstallPreference();
    FragmentManager mManager = getSupportFragmentManager();
    private static final int ADD_REPO_REQUEST_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRepoListViewModel = new ViewModelProvider(this).get(RepoListViewModel.class);
        installIfNeeded(mRepoListViewModel);

        ActivityRepoListBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_repo_list);
        mBinding.setLifecycleOwner(this);
        mBinding.setRepoListViewModel(mRepoListViewModel);

        mRepoListAdapter = new RepoListAdapter(this, mRepoListViewModel);
        mBinding.repoList.setAdapter(mRepoListAdapter);
        mBinding.repoList.setOnItemClickListener(mRepoListAdapter);
        mBinding.repoList.setOnItemLongClickListener(mRepoListAdapter);

        mRepoListViewModel.getShowToast().observe(this, this::showToastMsg);

        mRepoListViewModel.getAllRepos().observe(this, repoList -> {
            mRepoListAdapter.setRepos(repoList);
            mRepoListViewModel.setRepos(repoList);
        });

        mRepoListViewModel.getExecResult().observe(this, result -> {
            mRepoListViewModel.processExecResult(result);
        });

        pullToRefresh = findViewById(R.id.repoListLayout);
        pullToRefresh.setOnRefreshListener(() -> {
            mRepoListAdapter.refreshRepos();
            pullToRefresh.setRefreshing(false);
        });
    }

    /** Install packages if needed, or just ask for permissions */
    private void installIfNeeded(RepoListViewModel viewModel) {
        if (!viewModel.isInstalling()) {
            if (mInstallPref.isFirstRun()) {
                viewModel.setInstalling(true);
                runInstallFragment();
            } else {
                checkAndRequestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    private void runInstallFragment() {
        FragmentTransaction transaction = mManager.beginTransaction();
        InstallFragment fragment = new InstallFragment();
        transaction.add(R.id.repoListLayout,fragment);
        transaction.commit();
    }

    public void onPackagesInstalled(Boolean installed) {
        mRepoListViewModel.setInstalling(false);
        if (installed) {
            mInstallPref.updateInstallPreference();
            checkAndRequestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            showToastMsg(getString(R.string.install_failed));
            finishAffinity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_repo_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case R.id.menu_settings:
                intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                break;
            case R.id.menu_init_repo:
                intent = new Intent(this, AddRepoActivity.class);
                this.startActivity(intent);
                break;
            case R.id.menu_add_repo:
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, ADD_REPO_REQUEST_CODE);
                break;
            case R.id.menu_refresh:
                mRepoListAdapter.refreshRepos();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == ADD_REPO_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Get the URI of a repository to add
                Uri uri = intent.getData();
                String path = UriHelper.getStoragePathFromURI(this, uri);
                if (path == null) {
                    showToastMsg(getApplication().getString(R.string.storage_not_supported));
                } else if (Constants.isWritablePath(path)) {
                    mRepoListViewModel.addLocalRepo(path);
                } else {
                    showToastMsg(getApplication().getString(R.string.no_write_dir));
                }
            }
        }
    }

    /** Save install preference with version code */
    class InstallPreference {
        private final String PREFS_NAME = "mInstallPref";
        private final String PREF_VERSION_CODE_KEY = "version_code";
        private int currentVersionCode = BuildConfig.VERSION_CODE;

        Boolean isFirstRun() {
            final int DOESNT_EXIST = -1;
            // Get saved version code
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);
            // Check for first run or upgrade
            return currentVersionCode != savedVersionCode;
        }

        void updateInstallPreference() {
            // Update the shared preferences with the current version code
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
        }
    }
}

