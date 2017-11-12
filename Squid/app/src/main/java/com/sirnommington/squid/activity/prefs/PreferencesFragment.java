package com.sirnommington.squid.activity.prefs;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.sirnommington.squid.R;
import com.sirnommington.squid.services.Preferences;

/**
 * The settings fragment. Contains all settings.
 */
public class PreferencesFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.dev_preferences);

        final Activity context = getActivity();

        this.findPreference(this.getString(R.string.pref_reset)).setOnPreferenceClickListener(
            new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new Preferences(context).reset();
                    Toast.makeText(context, R.string.dev_settings_reset_complete, Toast.LENGTH_LONG).show();
                    context.finishAffinity();
                    return true;
                }
            });
    }
}
