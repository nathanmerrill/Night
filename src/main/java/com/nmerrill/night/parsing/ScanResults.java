package com.nmerrill.night.parsing;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class ScanResults {
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ImmutableList<Token> tokens;
    private final ImmutableList<ScanError> errors;

    public ScanResults(ImmutableList<Token> tokens, ImmutableList<ScanError> errors) {
        this.tokens = tokens;
        this.errors = errors;
    }

    public ScanResults(ImmutableList<Token> tokens) {
        this(tokens, ImmutableList.of());
    }

    public ImmutableList<Token> getTokens() {
        return tokens;
    }

    public ImmutableList<ScanError> getErrors() {
        return errors;
    }

    public boolean isSuccess(){
        return errors.isEmpty();
    }
}
