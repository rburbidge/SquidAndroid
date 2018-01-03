package com.sirnommington.squid.services.squid;

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
