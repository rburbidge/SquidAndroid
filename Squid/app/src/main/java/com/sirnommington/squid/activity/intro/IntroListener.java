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

    /**
     * User has completed adding their device.
     * @param hasOtherDevices True if the user has devices registered other than this one.
     */
    void addDeviceComplete(boolean hasOtherDevices);
}
