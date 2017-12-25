package com.sirnommington.squid.activity.common;

/**
 * The response of an asynchronous API call.
 * @param <TPayload> The type of payload returned if there was no error.
 */
public class AsyncResponse<TPayload> {
    public final TPayload payload;
    public final Object error;

    public static <TPayload> AsyncResponse<TPayload> create(TPayload payload) {
        return new AsyncResponse(payload, null);
    }

    public static <TPayload> AsyncResponse<TPayload> createError(Object error) {
        return new AsyncResponse(null, error);
    }

    private AsyncResponse(TPayload payload, Object error) {
        this.payload = payload;
        this.error = error;
    }
}
