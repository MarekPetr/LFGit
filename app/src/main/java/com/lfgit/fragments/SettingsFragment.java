package com.lfgit.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;

import com.lfgit.R;
import com.lfgit.executors.ExecListener;
import com.lfgit.executors.GitExec;
import com.lfgit.executors.GitExecListener;

import static com.lfgit.utilites.Logger.LogDebugMsg;

/**
 * Set preference settings
 * */
public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener, ExecListener, GitExecListener {

    private GitExec mGitExec = new GitExec(
            this,
            this,
            requireActivity().getApplication()
    );

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String prefValue = sharedPreferences.getString(key, "");
        LogDebugMsg(prefValue);
        if (key.equals(getString(R.string.git_username_key))) {
            mGitExec.setUsername(prefValue);
        } else if (key.equals(getString(R.string.git_email_key))) {
            mGitExec.setEmail(prefValue);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onExecStarted() {
    }

    @Override
    public void onExecFinished(String result, int errCode) {
    }

    @Override
    public void onError(String errorMsg) {
    }
}
