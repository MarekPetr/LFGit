package com.lfgit.activities;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.lfgit.R;
import com.lfgit.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
    }
}