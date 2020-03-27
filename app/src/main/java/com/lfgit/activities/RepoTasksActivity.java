package com.lfgit.activities;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RelativeLayout;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.lfgit.R;
import com.lfgit.adapters.RepoOperationsAdapter;
import com.lfgit.database.model.Repo;
import com.lfgit.databinding.ActivityRepoDetailBinding;
import com.lfgit.fragments.dialogs.CommitDialog;
import com.lfgit.fragments.dialogs.RemoteDialog;
import com.lfgit.fragments.dialogs.CredentialsDialog;
import com.lfgit.view_models.RepoTasksViewModel;


public class RepoTasksActivity extends BasicAbstractActivity {
    private RelativeLayout mRightDrawer;
    private DrawerLayout mDrawerLayout;
    private ActivityRepoDetailBinding mBinding;
    private CredentialsDialog mCredsDialog;
    private RemoteDialog mRemoteDialog;
    private CommitDialog mCommitDialog;
    private RepoTasksViewModel mRepoTasksViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_detail);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_repo_detail);
        mRepoTasksViewModel = new ViewModelProvider(this).get(RepoTasksViewModel.class);
        mBinding.setRepoTasksViewModel(mRepoTasksViewModel);
        mBinding.setLifecycleOwner(this);

        setupDrawer();
        mBinding.taskResult.setMovementMethod(new ScrollingMovementMethod());

        Repo repo = (Repo) getIntent().getSerializableExtra(Repo.TAG);
        mRepoTasksViewModel.setRepo(repo);

        mRepoTasksViewModel.getExecPending().observe(this, isPending -> {
            if (isPending) {
                showProgressDialog();
            } else {
                hideProgressDialog();
            }
        });
        mRepoTasksViewModel.getPromptCredentials().observe(this, promptCredentials -> {
            if (promptCredentials) {
                showDialog(mCredsDialog, "creds_dialog");
            } else {
                hideDialog(mCredsDialog);
            }
        });

        mRepoTasksViewModel.getPromptAddRemote().observe(this, promptRemote -> {
            if (promptRemote) {
                showDialog(mRemoteDialog, "remote_dialog");
            } else {
                hideDialog(mRemoteDialog);
            }
        });

        mRepoTasksViewModel.getPromptCommit().observe(this, promptCommit -> {
            if (promptCommit) {
                //showCommitDialog();
                showDialog(mCommitDialog, "commit_dialog");
            } else {
                hideDialog(mCommitDialog);
            }
        });

        mRepoTasksViewModel.getShowToast().observe(this, this::showToastMsg);
    }

    private void hideDialog(DialogFragment dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void showDialog(DialogFragment dialog, String tag) {
        dialog = RemoteDialog.newInstance(mRepoTasksViewModel);
        FragmentTransaction ft = getFragmentTransaction(tag);
        dialog.show(ft, tag);
    }

    private FragmentTransaction getFragmentTransaction(String tag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("commit_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        return ft;
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

        RepoOperationsAdapter mDrawerAdapter = new RepoOperationsAdapter(this, mRepoTasksViewModel);
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
