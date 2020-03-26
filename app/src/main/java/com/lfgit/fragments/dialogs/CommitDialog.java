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

import com.lfgit.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CommitDialog extends DialogFragment {
    private CommitDialogListener mListener;
    private EditText mCommitMsgEditText;
    private Button mEnterButton;

    public CommitDialog() {
        // empty constructor required
    }

    public static CommitDialog newInstance(CommitDialogListener listener) {
        CommitDialog dialog = new CommitDialog();
        dialog.mListener = listener;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.commit_dialog_layout, null, false);

        mCommitMsgEditText = view.findViewById(R.id.commitMsgEditText);
        mEnterButton = view.findViewById(R.id.enterButton);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        alertDialogBuilder.setView(view);
        mEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commitMsg = mCommitMsgEditText.getText().toString();
                mListener.handleCommitMsg(commitMsg);
            }
        });
        return alertDialogBuilder.create();
    }

    @Override
    public void onCancel(@NotNull DialogInterface dialog) {
        super.onCancel(dialog);
        mListener.onCancelCommitDialog();
    }

    public interface CommitDialogListener {
        void handleCommitMsg(String commitMsg);
        void onCancelCommitDialog();
    }
}
