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

public class RemoteDialog extends DialogFragment {
    private RepoTasksViewModel viewModel;
    private EditText mRemoteURL;

    public RemoteDialog() {
        // empty constructor required
    }

    public static RemoteDialog newInstance() {
        RemoteDialog dialog = new RemoteDialog();
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.add_remote_layout, null, false);

        viewModel = new ViewModelProvider(requireActivity()).get(RepoTasksViewModel.class);

        mRemoteURL = view.findViewById(R.id.remotePathEditText);
        Button mEnterButton = view.findViewById(R.id.enterButton);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        alertDialogBuilder.setView(view);
        mEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String remoteURL = mRemoteURL.getText().toString();
                viewModel.handleRemoteURL(remoteURL);
            }
        });
        return alertDialogBuilder.create();
    }

    @Override
    public void onCancel(@NotNull DialogInterface dialog) {
        super.onCancel(dialog);
        viewModel.startState();
    }
}
