package com.lfgit.fragments.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.lfgit.R;
import com.lfgit.view_models.RepoTasksViewModel;

import org.jetbrains.annotations.NotNull;

public abstract class EnterTextDialog extends DialogFragment {
    RepoTasksViewModel viewModel;

    public EnterTextDialog() {
        // empty constructor required
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = requireActivity();
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(getDialogLayoutID(), null, false);

        viewModel = new ViewModelProvider(requireActivity()).get(RepoTasksViewModel.class);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);

        Button enterButton = view.findViewById(R.id.enterButton);

        enterButton.setOnClickListener(buttonView -> {
            EditText editText = view.findViewById(R.id.editText);
            String branch = editText.getText().toString();
            handleText(branch);
        });

        return builder.create();
    }

    @Override
    public void onCancel(@NotNull DialogInterface dialog) {
        viewModel.startState();
        super.onCancel(dialog);
    }

    /** Overwrite to handle EditText text */
    abstract void handleText(String text);

    /** Overwrite to set a dialog layout */
    abstract int getDialogLayoutID();
}