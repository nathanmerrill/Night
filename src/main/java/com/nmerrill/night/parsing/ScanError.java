package com.nmerrill.night.parsing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class ScanError {
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final String message;
    private final int position;

    public ScanError(String message, int position) {
        this.message = message;
        this.position = position;
    }
    public String getMessage() {
        return message;
    }
    public int getPosition(){
        return position;
    }

    @Override
    public String toString() {
        return message;
    }
}
