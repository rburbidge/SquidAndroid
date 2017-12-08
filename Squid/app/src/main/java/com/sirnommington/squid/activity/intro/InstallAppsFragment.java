package com.sirnommington.squid.activity.intro;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sirnommington.squid.R;

/**
 * Shows options to install the app on other devices.
 */
public class InstallAppsFragment extends android.app.Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_install_apps, container, false);
        view.findViewById(R.id.install_android).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                installAndroidApp();
            }
        });
        view.findViewById(R.id.install_chrome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                installChromeExtension();
            }
        });
        return view;
    }

    /**
     * Opens the Google Play store.
     */
    private void installAndroidApp() {
        final String appPackageName = getActivity().getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            // This exception will be thrown if the Play Store is not installed
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    /**
     * Opens the Chrome Web Store.
     */
    private void installChromeExtension() {
        final Intent openChromeWebStore = new Intent(
                Intent.ACTION_VIEW, Uri.parse(this.getResources().getString(R.string.chrome_ext_url)));
        this.startActivity(openChromeWebStore);
    }
}
