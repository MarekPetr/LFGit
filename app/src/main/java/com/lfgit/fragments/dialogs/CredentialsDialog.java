package com.lfgit.fragments.dialogs;

import android.app.Activity;
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
import androidx.lifecycle.ViewModelStoreOwner;

import com.lfgit.R;
import com.lfgit.view_models.RepoTasksViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CredentialsDialog extends DialogFragment {
    private RepoTasksViewModel viewModel;
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private View view;

    public CredentialsDialog() {
        // empty constructor required
    }

    public static CredentialsDialog newInstance() {
        return new CredentialsDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = requireActivity();
        LayoutInflater inflater = LayoutInflater.from(activity);
        view = inflater.inflate(R.layout.credentials_dialog, null, false);

        viewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(RepoTasksViewModel.class);

        mUsernameEditText = view.findViewById(R.id.usernameEditText);
        mPasswordEditText = view.findViewById(R.id.passwordEditText);
        Button mEnterButton = view.findViewById(R.id.enterButton);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireActivity());
        alertDialogBuilder.setView(view);
        mEnterButton.setOnClickListener(buttonView -> {
            String username = mUsernameEditText.getText().toString();
            String password = mPasswordEditText.getText().toString();
            viewModel.handleCredentials(username, password);
        });
        return alertDialogBuilder.create();
    }

    @Override
    public void onCancel(@NotNull DialogInterface dialog) {
        viewModel.startState();
        super.onCancel(dialog);
    }
}
