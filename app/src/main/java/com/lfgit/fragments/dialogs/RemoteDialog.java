package com.lfgit.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.lfgit.R;
import com.lfgit.view_models.RepoTasksViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
        return R.layout.add_remote_dialog;
    }
}
