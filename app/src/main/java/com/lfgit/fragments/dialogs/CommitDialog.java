package com.lfgit.fragments.dialogs;
import com.lfgit.R;

public class CommitDialog extends EnterTextDialog {

    public CommitDialog() {
        // empty constructor required
    }

    public static CommitDialog newInstance() {
        return new CommitDialog();
    }

    @Override
    void handleText(String text) {
        viewModel.handleCommitMsg(text);
    }

    @Override
    int getDialogLayoutID() {
        return R.layout.commit_dialog;
    }
}
