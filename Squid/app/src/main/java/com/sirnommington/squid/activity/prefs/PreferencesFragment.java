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
    private boolean hasDevOptionsBeenEnabled = false;
    private Preferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.preferences = new Preferences(getActivity());
        this.addPreferencesFromResource(R.xml.preferences);
    }

    /**
     * Adds developer preferences if they are enabled.
     * This is done in onResume() because we want to update the activity if dev mode was changed in AboutActivity.
     */
    @Override
    public void onResume() {
        super.onResume();

        // Prevent duplicate dev options from being added to the activity
        if(hasDevOptionsBeenEnabled) {
            return;
        }

        // Add dev prefs if enabled
        if(this.preferences.isDevMode()) {
            this.addDevPreferences();
            this.hasDevOptionsBeenEnabled = true;
        }
    }

    /**
     * Adds developer preferences to the activity.
     */
    private void addDevPreferences() {
        this.addPreferencesFromResource(R.xml.dev_preferences);

        final Activity context = getActivity();
        this.findPreference(this.getString(R.string.pref_reset)).setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        preferences.reset();
                        Toast.makeText(context, R.string.dev_settings_reset_complete, Toast.LENGTH_LONG).show();
                        context.finishAffinity();
                        return true;
                    }
                });
    }
}
