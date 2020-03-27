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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.lfgit.BuildConfig;
import com.lfgit.R;
import com.lfgit.adapters.RepoListAdapter;
import com.lfgit.databinding.ActivityRepoListBinding;
import com.lfgit.fragments.InstallFragment;
import com.lfgit.utilites.Constants;
import com.lfgit.utilites.UriHelper;
import com.lfgit.view_models.AddRepoViewModel;
import com.lfgit.view_models.RepoListViewModel;

public class RepoListActivity extends BasicAbstractActivity implements InstallFragment.FragmentCallback {
    private ActivityRepoListBinding mBinding;
    private AddRepoViewModel mAddRepoViewModel;
    private RepoListAdapter mRepoListAdapter;
    private InstallPreference mInstallPref = new InstallPreference();
    FragmentManager mManager = getSupportFragmentManager();
    private String mInstallTag = "install";

    private static final int ADD_REPO_REQUEST_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mInstallPref.assetsInstalled()) {
            checkAndRequestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            runInstallFragment();
        }

        RepoListViewModel repoListViewModel = new ViewModelProvider(this).get(RepoListViewModel.class);
        mAddRepoViewModel = new ViewModelProvider(this).get(AddRepoViewModel.class);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_repo_list);
        mBinding.setLifecycleOwner(this);
        mBinding.setRepoListViewModel(repoListViewModel);

        mRepoListAdapter = new RepoListAdapter(this, repoListViewModel);
        mBinding.repoList.setAdapter(mRepoListAdapter);
        mBinding.repoList.setOnItemClickListener(mRepoListAdapter);
        mBinding.repoList.setOnItemLongClickListener(mRepoListAdapter);

        repoListViewModel.getAllRepos().observe(this, repoList -> {
            mRepoListAdapter.setRepos(repoList);
            mAddRepoViewModel.setAllRepos(repoList);
        });
    }

    private void runInstallFragment() {
        FragmentTransaction transaction = mManager.beginTransaction();
        InstallFragment fragment = new InstallFragment();
        fragment.setCallback(this);
        transaction.add(R.id.repoListLayout,fragment);
        transaction.addToBackStack(mInstallTag);
        transaction.commit();
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
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void removeFragment() {
        Fragment fragment = mManager.findFragmentByTag(mInstallTag);
        if(fragment != null) {
            mManager.popBackStack();
        }
        mInstallPref.updateInstallPreference();
        checkAndRequestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == ADD_REPO_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = intent.getData();
                String path = UriHelper.getDirPath(this, uri);
                if (path != null) {
                    if (mAddRepoViewModel.addLocalRepo(path)
                            == Constants.AddRepo.ALREADY_ADDED) {
                        showToastMsg("Repository already added");
                    }
                } else {
                    showToastMsg(getString (R. string. browse_only_primary));
                }
            }
        }
    }

    class InstallPreference {
        private final String PREFS_NAME = "mInstallPref";
        private final String PREF_VERSION_CODE_KEY = "version_code";
        private int currentVersionCode = BuildConfig.VERSION_CODE;

        Boolean assetsInstalled() {
            final int DOESNT_EXIST = -1;

            // Get saved version code
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

            // Check for first run or upgrade
            if (currentVersionCode == savedVersionCode) {
                // This is just a normal run
                return true;
            } else if (savedVersionCode == DOESNT_EXIST) {
                // This is a new install (or the user cleared the shared preferences)
            } else if (currentVersionCode > savedVersionCode) {
                // This is an upgrade
            }
            return false;
        }

        void updateInstallPreference() {
            // Update the shared preferences with the current version code
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
        }
    }
}

