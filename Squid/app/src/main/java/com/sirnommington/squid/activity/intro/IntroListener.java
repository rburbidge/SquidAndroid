package com.sirnommington.squid.activity.intro;

/**
 * Interface for listening to intro fragments.
 */
public interface IntroListener {
    /**
     * User is done with description fragment.
     */
    void descriptionComplete();

    /**
     * User has completed signing-in on sign-in fragment.
     */
    void signInComplete();
}
