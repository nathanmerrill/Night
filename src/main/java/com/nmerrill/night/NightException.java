package com.nmerrill.night;

public class NightException extends RuntimeException {

    /**
     * {@inheritDoc}
     */
    public NightException() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public NightException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public NightException(String message, Throwable cause) {
        super(message, cause);
    }


    /**
     * {@inheritDoc}
     */
    public NightException(Throwable cause) {
        super(cause);
    }

}
