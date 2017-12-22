package com.sirnommington.squid.activity.common;

import com.sirnommington.squid.services.google.GoogleSignIn;

/**
 * Provides access to GoogleSignIn instance.
 */
public interface GoogleSignInProvider {
    GoogleSignIn getGoogleSignIn();
}
