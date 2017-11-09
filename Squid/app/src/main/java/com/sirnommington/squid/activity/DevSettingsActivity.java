package com.sirnommington.squid.activity;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.sirnommington.squid.R;
import com.sirnommington.squid.services.Preferences;

/**
 * Developer-specific settings that are hidden from normal users.
 */
public class DevSettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.activity_dev_settings);

        final PreferenceActivity thiz = this;

        this.findPreference(this.getString(R.string.pref_reset)).setOnPreferenceClickListener(
            new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new Preferences(thiz).reset();
                    Toast.makeText(thiz, R.string.dev_settings_reset_complete, Toast.LENGTH_LONG).show();
                    return true;
                }
            });
    }
}
