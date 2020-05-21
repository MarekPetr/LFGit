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

import static com.lfgit.utilites.Logger.LogMsg;

/**
 * An activity implementing Git tasks user interface.
 */
public class RepoTasksActivity extends BasicAbstractActivity {
    private RelativeLayout mRightDrawer;
    private DrawerLayout mDrawerLayout;
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

        ActivityRepoTasksBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_repo_tasks);
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

        mRepoTasksViewModel.getPromptCredentials().observe(this, show -> {
            toggleDialog(show, mCredsDialog, "credsDialog");
        });

        mRepoTasksViewModel.getPromptAddRemote().observe(this, show -> {
            toggleDialog(show, mRemoteDialog, "remoteDialog");
        });

        mRepoTasksViewModel.getPromptCommit().observe(this, show -> {
            toggleDialog(show, mCommitDialog, "commitDialog");
        });

        mRepoTasksViewModel.getPromptCheckout().observe(this, show -> {
            toggleDialog(show, mCheckoutDialog, "checkoutDialog");
        });

        mRepoTasksViewModel.getPromptPattern().observe(this, show -> {
            toggleDialog(show, mPatternDialog, "patternDialog");
        });

        mRepoTasksViewModel.getShowToast().observe(this, this::showToastMsg);
    }

    private void hideDialog(String tag) {
        Fragment dialog = getSupportFragmentManager().findFragmentByTag(tag);
        if (dialog != null) {
            DialogFragment df = (DialogFragment) dialog;
            df.dismiss();
        }
    }

    private void showDialog(DialogFragment dialog, String tag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(tag);
        dialog.show(ft, tag);
    }

    private void toggleDialog(Boolean show, DialogFragment dialog, String tag) {
        if (show) {
            showDialog(dialog, tag);
        } else {
            hideDialog(tag);
        }
    }

    private void setupDialogs() {
        mCheckoutDialog = CheckoutDialog.newInstance();
        mCommitDialog = CommitDialog.newInstance();
        mCredsDialog = CredentialsDialog.newInstance();
        mRemoteDialog = RemoteDialog.newInstance();
        mPatternDialog = PatternDialog.newInstance();
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

}
