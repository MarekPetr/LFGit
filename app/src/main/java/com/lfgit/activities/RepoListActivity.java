package com.lfgit.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.lfgit.interfaces.FragmentCallback;
import com.lfgit.view_models.RepoListViewModel;

public class RepoListActivity extends BasicAbstractActivity implements FragmentCallback {
    private ActivityRepoListBinding mBinding;
    private RepoListAdapter mRepoListAdapter;
    private InstallPreference installPref = new InstallPreference();
    FragmentManager mManager = getSupportFragmentManager();
    private String installTag = "install";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (installPref.assetsInstalled()) {
            checkAndRequestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            runInstallFragment();
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

    private void runInstallFragment() {
        FragmentTransaction transaction = mManager.beginTransaction();
        InstallFragment fragment = new InstallFragment();
        fragment.setCallback(this);
        transaction.add(R.id.repoListLayout,fragment);
        transaction.addToBackStack(installTag);
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
        Class intent_class;
        switch(item.getItemId()) {
            case R.id.menu_init_repo:
                intent_class = InitRepoActivity.class;
                break;
            case R.id.menu_settings:
                intent_class = SettingsActivity.class;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        intent = new Intent(this, intent_class);
        this.startActivity(intent);
        return true;
    }

    @Override
    public void removeFragment() {
        Fragment fragment = mManager.findFragmentByTag(installTag);
        if(fragment != null) {
            mManager.popBackStack();
        }
        installPref.updateInstallPreference();
        checkAndRequestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
}

