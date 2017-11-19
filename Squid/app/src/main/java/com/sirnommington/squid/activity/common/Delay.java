package com.sirnommington.squid.activity.common;

public class Delay {
    /**
     * Runs an operation and returns its result within a minimum of a number of milliseconds.
     * e.g. Use when you want a UI component to appear for a minimum amount of time.
     * *SHOULD ONLY BE USED ON BACKGROUND THREADS*
     * @param run The operation.
     * @param timeMillis The minimum time in milliseconds.
     * @param <T> The operation result type.
     * @return The operation result.
     */
    public static <T> T delay(Run<T> run, long timeMillis) {
        final long startMillis = System.currentTimeMillis();
        final T result =  run.run();
        final long endMillis = System.currentTimeMillis();
        final long elapsedMillis = endMillis - startMillis;
        final long remainingMillis = timeMillis - elapsedMillis;
        if (remainingMillis > 0) {
            try {
                Thread.sleep(remainingMillis);
            } catch (InterruptedException e) {
                // Do nothing
            }
        }

        return result;
    }

    public interface Run<T> {
        T run();
    }
}

