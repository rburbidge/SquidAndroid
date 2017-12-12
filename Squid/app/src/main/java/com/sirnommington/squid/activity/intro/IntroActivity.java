package com.sirnommington.squid.activity.intro;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sirnommington.squid.R;
import com.sirnommington.squid.activity.IntentExtras;
import com.sirnommington.squid.activity.RequestCode;
import com.sirnommington.squid.activity.share.AddOtherDeviceActivity;
import com.sirnommington.squid.activity.share.ShareLinkActivity;
import com.sirnommington.squid.services.Preferences;
import com.sirnommington.squid.services.google.GoogleSignIn;

/**
 * Shows an intro flow to the user, include sign-in, device registration, etc.
 *
 * Callers can deep-link into a specific intro step. E.g.:
 * <pre>
 * Intent intent = new Intent(this, IntroActivity.class);
 * intent.putExtra(IntentExtras.INTRO_STEP, IntroSteps.SIGN_IN);
 * this.startActivity(intent);
 * </pre>
 */
public class IntroActivity extends AppCompatActivity implements IntroListener, GoogleSignInProvider {

    private GoogleSignIn googleSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        this.googleSignIn = new GoogleSignIn(this);

        final Fragment fragment = this.getInitialIntroFragment();
        final FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.add(R.id.intro_content, fragment, fragment.getClass().getSimpleName());
        tx.commit();
    }

    /**
     * Gets the initial fragment for the intro activity based on the IntentExtras.INTRO_STEP intent parameter.
     * The default is DescriptionFragment.
     * @return The fragment that should launch the said activity.
     * @throws IllegalArgumentException if the intro step has not been mapped to an intro fragment.
     */
    private Fragment getInitialIntroFragment() {
        final int introStep = this.getIntent().getIntExtra(IntentExtras.INTRO_STEP, IntroStep.DESCRIPTION);
        switch(introStep) {
            case IntroStep.DESCRIPTION:
                return new DescriptionFragment();
            case IntroStep.SIGN_IN:
                return new SignInFragment();
            case IntroStep.ADD_DEVICE:
                return new AddDeviceFragment();
        }
        throw new IllegalArgumentException(String.format(
                "Unhandled %0=%1. Did you add your intro fragment to getStepFragment()?",
                IntentExtras.INTRO_STEP, introStep));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RequestCode.ADD_OTHER_DEVICE) {
            this.introComplete();
        }
    }

    public void descriptionComplete() {
        this.replaceBottomFragment(new SignInFragment());
    }

    public void signInComplete() {
        this.replaceBottomFragment(new AddDeviceFragment());
    }

    /**
     * Sets the app as being initialized and launches the next activity.
     *
     * If the user has other devices, the completes the intro. Otherwise, shows the InstallAppsFragment.
     */
    public void addDeviceComplete(boolean hasOtherDevices) {
        final Preferences preferences = new Preferences(this);
        preferences.setInitialized();

        if(hasOtherDevices) {
            this.introComplete();
        } else {
            this.startActivityForResult(
                AddOtherDeviceActivity.createIntent(this, hasOtherDevices),
                RequestCode.ADD_OTHER_DEVICE);
        }
    }

    public void introComplete() {
        this.startActivity(ShareLinkActivity.createMainIntent(this));
        this.finish();
    }

    private void replaceBottomFragment(Fragment fragment) {
        final FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.setCustomAnimations(R.animator.intro_enter, R.animator.intro_exit);
        tx.replace(R.id.intro_content, fragment, fragment.getClass().getSimpleName());
        tx.commit();
    }

    public GoogleSignIn getGoogleSignIn() {
        return this.googleSignIn;
    }
}
