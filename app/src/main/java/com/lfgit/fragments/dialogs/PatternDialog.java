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

public class PatternDialog extends DialogFragment {
    private RepoTasksViewModel viewModel;
    private EditText mPatternEditText;
    private Button mEnterButton;

    public PatternDialog() {
        // empty constructor required
    }

    public static PatternDialog newInstance() {
        PatternDialog dialog = new PatternDialog();
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.pattern_dialog_layout, null, false);

        viewModel = new ViewModelProvider(requireActivity()).get(RepoTasksViewModel.class);

        mPatternEditText = view.findViewById(R.id.patternEditText);
        mEnterButton = view.findViewById(R.id.enterButton);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        alertDialogBuilder.setView(view);
        mEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commitMsg = mPatternEditText.getText().toString();
                viewModel.handlePattern(commitMsg);
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
