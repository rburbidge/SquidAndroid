package com.sirnommington.squid.services.squid.contracts;

/**
 * Request to send a URL to a device.
 */
public class SendUrlRequest {
    /**
     * The URL to send.
     */
    public final String url;

    public SendUrlRequest(String url) {
        this.url = url;
    }
}
