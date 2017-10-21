package com.sirnommington.squid.activity;

/**
 * The response of an asynchronous API call.
 * @param <TPayload> The type of payload returned if there was no error.
 */
public class AsyncResponse<TPayload> {
    public final TPayload payload;
    public final Object error;

    public AsyncResponse(TPayload payload, Object error) {
        this.payload = payload;
        this.error = error;
    }
}
