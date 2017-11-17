package com.sirnommington.squid.activity.intro;

import com.sirnommington.squid.services.google.GoogleSignIn;

/**
 * Provides access to GoogleSignIn instance.
 */
public interface GoogleSignInProvider {
    GoogleSignIn getGoogleSignIn();
}
