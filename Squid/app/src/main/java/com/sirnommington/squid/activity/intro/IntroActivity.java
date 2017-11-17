package com.sirnommington.squid.activity.intro;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sirnommington.squid.R;
import com.sirnommington.squid.services.Preferences;

public class IntroActivity extends AppCompatActivity implements IntroListener {
    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        this.preferences = new Preferences(this);

        final DescriptionFragment fragment = new DescriptionFragment();
        final FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.add(R.id.intro_content, fragment, fragment.getClass().getSimpleName());
        tx.commit();
    }

    public void descriptionComplete() {
        this.replaceBottomFragment(new SignInFragment());
    }

    public void signInComplete() {
        this.replaceBottomFragment(new AddDeviceFragment());
    }

    private void replaceBottomFragment(Fragment fragment) {
        final FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.replace(R.id.intro_content, fragment, fragment.getClass().getSimpleName());
        tx.commit();
    }
}
