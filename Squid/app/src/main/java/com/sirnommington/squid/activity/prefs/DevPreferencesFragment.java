package com.sirnommington.squid.activity.prefs;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.sirnommington.squid.R;
import com.sirnommington.squid.services.Preferences;

/**
 * Developer-specific settings that are hidden from normal users.
 */
public class DevPreferencesFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.dev_preferences);

        final Context context = getActivity();

        this.findPreference(this.getString(R.string.pref_reset)).setOnPreferenceClickListener(
            new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new Preferences(context).reset();
                    Toast.makeText(context, R.string.dev_settings_reset_complete, Toast.LENGTH_LONG).show();
                    return true;
                }
            });
    }
}
