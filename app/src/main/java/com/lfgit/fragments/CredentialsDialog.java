package com.lfgit.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.lfgit.R;

import java.util.Objects;

public class CredentialsDialog extends DialogFragment {
    private CredentialsDialogListener mListener;
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mEnterButton;

    public CredentialsDialog() {
        // empty constructor required
    }

    public static CredentialsDialog newInstance(CredentialsDialogListener listener) {
        CredentialsDialog dialog = new CredentialsDialog();
        dialog.mListener = listener;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.credentials_dialog_layout, null, false);

        mUsernameEditText = view.findViewById(R.id.usernameEditText);
        mPasswordEditText = view.findViewById(R.id.passwordEditText);
        mEnterButton = view.findViewById(R.id.enterButton);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        alertDialogBuilder.setView(view);
        mEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsernameEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                mListener.getCredentials(username, password);
            }
        });
        return alertDialogBuilder.create();
    }

    public interface CredentialsDialogListener {
        void getCredentials(String username, String password);
    }
}
