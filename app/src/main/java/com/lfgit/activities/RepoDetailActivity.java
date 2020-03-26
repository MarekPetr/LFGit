package com.lfgit.activities;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RelativeLayout;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.lfgit.R;
import com.lfgit.adapters.RepoOperationsAdapter;
import com.lfgit.database.model.Repo;
import com.lfgit.databinding.ActivityRepoDetailBinding;
import com.lfgit.fragments.AddRemoteDialog;
import com.lfgit.fragments.CredentialsDialog;
import com.lfgit.view_models.RepoDetailViewModel;


public class RepoDetailActivity extends BasicAbstractActivity {
    private RelativeLayout mRightDrawer;
    private DrawerLayout mDrawerLayout;
    private ActivityRepoDetailBinding mBinding;
    private CredentialsDialog mCredsDialog;
    private AddRemoteDialog mAddRemoteDialog;
    private RepoDetailViewModel mRepoDetailViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_detail);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_repo_detail);
        mRepoDetailViewModel = new ViewModelProvider(this).get(RepoDetailViewModel.class);
        mBinding.setRepoDetailViewModel(mRepoDetailViewModel);
        mBinding.setLifecycleOwner(this);

        setupDrawer();
        mBinding.taskResult.setMovementMethod(new ScrollingMovementMethod());

        Repo repo = (Repo) getIntent().getSerializableExtra(Repo.TAG);
        mRepoDetailViewModel.setRepo(repo);

        mRepoDetailViewModel.getExecPending().observe(this, isPending -> {
            if (isPending) {
                showProgressDialog();
            } else {
                hideProgressDialog();
            }
        });
        mRepoDetailViewModel.getPromptCredentials().observe(this, promptCredentials -> {
            if (promptCredentials) {
                showCredentialsDialog();
            } else {
                hideCredentialsDialog();
            }
        });

        mRepoDetailViewModel.getPromptAddRemote().observe(this, promptRemote -> {
            if (promptRemote) {
                showAddRemoteDialog();
            } else {
                hideAddRemoteDialog();
            }
        });

        mRepoDetailViewModel.getShowToast().observe(this, message -> {
            showToastMsg(message);
        });

    }

    private void showCredentialsDialog() {
        mCredsDialog = CredentialsDialog.newInstance(mRepoDetailViewModel);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("creds_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        mCredsDialog.show(ft, "creds_dialog");
    }

    private void hideCredentialsDialog() {
        if (mCredsDialog != null) {
            mCredsDialog.dismiss();
        }
    }

    private void showAddRemoteDialog() {
        mAddRemoteDialog = AddRemoteDialog.newInstance(mRepoDetailViewModel);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("remote_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        mAddRemoteDialog.show(ft, "remote_dialog");
    }

    private void hideAddRemoteDialog() {
        if (mAddRemoteDialog != null) {
            mAddRemoteDialog.dismiss();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_repo_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toggleDrawer) {
            if (mDrawerLayout.isDrawerOpen(mRightDrawer)) {
                closeDrawer();
            } else {
                openDrawer();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawer() {
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mRightDrawer = findViewById(R.id.rightDrawer);
        ListView mRepoOperationList = findViewById(R.id.repoOperationList);

        RepoOperationsAdapter mDrawerAdapter = new RepoOperationsAdapter(this, mRepoDetailViewModel);
        mRepoOperationList.setAdapter(mDrawerAdapter);
        mRepoOperationList.setOnItemClickListener(mDrawerAdapter);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mRightDrawer);
    };

    public void openDrawer() {
        mDrawerLayout.openDrawer(mRightDrawer);
    }
}
