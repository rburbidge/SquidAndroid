package com.sirnommington.squid.activity.intro;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sirnommington.squid.R;
import com.sirnommington.squid.services.google.GoogleSignIn;

/**
 * Shows an intro flow to the user, include sign-in, device registration, etc.
 */
public class IntroActivity extends AppCompatActivity implements IntroListener, GoogleSignInProvider {

    private GoogleSignIn googleSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        this.googleSignIn = new GoogleSignIn(this);

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

    public GoogleSignIn getGoogleSignIn() {
        return this.googleSignIn;
    }
}
