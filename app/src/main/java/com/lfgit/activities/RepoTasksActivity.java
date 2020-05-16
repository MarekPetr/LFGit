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
import com.lfgit.adapters.RepoTasksAdapter;
import com.lfgit.database.model.Repo;
import com.lfgit.databinding.ActivityRepoTasksBinding;
import com.lfgit.fragments.dialogs.*;
import com.lfgit.view_models.RepoTasksViewModel;

/**
 * An activity implementing Git tasks user interface.
 */
public class RepoTasksActivity extends BasicAbstractActivity {
    private RelativeLayout mRightDrawer;
    private DrawerLayout mDrawerLayout;
    private ActivityRepoTasksBinding mBinding;
    private CredentialsDialog mCredsDialog;
    private RemoteDialog mRemoteDialog;
    private CommitDialog mCommitDialog;
    private CheckoutDialog mCheckoutDialog;
    private PatternDialog mPatternDialog;
    private RepoTasksViewModel mRepoTasksViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_tasks);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_repo_tasks);
        mRepoTasksViewModel = new ViewModelProvider(this).get(RepoTasksViewModel.class);
        mBinding.setRepoTasksViewModel(mRepoTasksViewModel);
        mBinding.setLifecycleOwner(this);

        setupDrawer();
        setupDialogs();
        mBinding.taskResult.setMovementMethod(new ScrollingMovementMethod());

        Repo repo = (Repo) getIntent().getSerializableExtra(Repo.TAG);
        mRepoTasksViewModel.setRepo(repo);
        if (repo != null) {setTitle(repo.getDisplayName());}

        mRepoTasksViewModel.getNoRepo().observe(this, message -> {
            showToastMsg(message);
            finish();
        });

        mRepoTasksViewModel.getExecResult().observe(this, mRepoTasksViewModel::processExecResult);

        mRepoTasksViewModel.getExecPending().observe(this, this::toggleProgressDialog);

        mRepoTasksViewModel.getPromptCredentials().observe(this, promptCredentials -> {
            toggleDialog(promptCredentials, mCredsDialog, "creds_dialog");
        });

        mRepoTasksViewModel.getPromptAddRemote().observe(this, promptRemote -> {
            toggleDialog(promptRemote, mRemoteDialog, "remote_dialog");
        });

        mRepoTasksViewModel.getPromptCommit().observe(this, promptCommit -> {
            toggleDialog(promptCommit, mCommitDialog, "commit_dialog");
        });

        mRepoTasksViewModel.getPromptCheckout().observe(this, promptCheckout -> {
            toggleDialog(promptCheckout, mCheckoutDialog, "checkout_dialog");
        });
        mRepoTasksViewModel.getPromptPattern().observe(this, promptPattern -> {
            toggleDialog(promptPattern, mPatternDialog, "prompt_dialog");
        });

        mRepoTasksViewModel.getShowToast().observe(this, this::showToastMsg);
    }

    private void hideDialog(DialogFragment dialog) {
        if (dialog != null && dialog.getDialog().isShowing()) {
            dialog.dismiss();
        }
    }

    private void showDialog(DialogFragment dialog, String tag) {
        FragmentTransaction ft = getFragmentTransaction();
        dialog.show(ft, tag);
    }

    private void toggleDialog(Boolean show, DialogFragment dialog, String tag) {
        if (show) {
            showDialog(dialog, tag);
        } else {
            hideDialog(dialog);
        }
    }

    private void setupDialogs() {
        mCommitDialog = CommitDialog.newInstance();
        mCredsDialog = CredentialsDialog.newInstance();
        mRemoteDialog = RemoteDialog.newInstance();
        mCheckoutDialog = CheckoutDialog.newInstance();
        mPatternDialog = PatternDialog.newInstance();
    }

    private FragmentTransaction getFragmentTransaction() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
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

        RepoTasksAdapter mDrawerAdapter = new RepoTasksAdapter(this, mRepoTasksViewModel);
        mRepoOperationList.setAdapter(mDrawerAdapter);
        mRepoOperationList.setOnItemClickListener(mDrawerAdapter);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mRightDrawer);
    };

    public void openDrawer() {
        mDrawerLayout.openDrawer(mRightDrawer);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideDialog(mCommitDialog);
        hideDialog(mCheckoutDialog);
        hideDialog(mRemoteDialog);
        hideDialog(mCheckoutDialog);
        hideDialog(mPatternDialog);
    }
}
