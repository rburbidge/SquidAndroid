package com.sirnommington.squid.services.common;

/**
 * An HTTP Response.
 * @param <TBody> The body type.
 */
public class HttpResponse<TBody> {
    public final int statusCode;
    public final TBody body;

    public HttpResponse(int statusCode, TBody body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    /**
     * Returns true if status code indicates success.
     */
    public boolean isSuccess() {
        switch(this.statusCode) {
            case 200:
            case 302:
                return true;
            default:
                return false;
        }
    }
}
