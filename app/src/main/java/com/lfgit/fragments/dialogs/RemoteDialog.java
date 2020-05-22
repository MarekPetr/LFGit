package com.lfgit.fragments.dialogs;

import com.lfgit.R;

public class RemoteDialog extends EnterTextDialog {
    public RemoteDialog() {
        // empty constructor required
    }

    public static RemoteDialog newInstance() {
        return new RemoteDialog();
    }

    @Override
    void handleText(String text) {
        viewModel.handleRemoteURL(text);
    }

    @Override
    int getDialogLayoutID() {
        return R.layout.remote_dialog;
    }
}
