package com.lfgit.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;

import com.lfgit.R;
import com.lfgit.executors.ExecCallback;
import com.lfgit.executors.GitExec;

import static com.lfgit.utilites.Logger.LogMsg;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener, ExecCallback {

    private GitExec gitExec = new GitExec(this);

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String prefValue = sharedPreferences.getString(key, "");
        LogMsg(prefValue);
        if (key.equals(getString(R.string.git_username_key))) {
            gitExec.setUsername(prefValue);
        } else if (key.equals(getString(R.string.git_email_key))) {
            gitExec.setEmail(prefValue);
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
    public void passResult(String result) {
    }

    @Override
    public void passErrCode(int errCode, String task) {

    }
}
