package com.nmerrill.night;

import org.checkerframework.checker.nullness.qual.Nullable;

public class Result<T> {

    private final @Nullable String error;
    private final int position;
    private final @Nullable T value;

    private Result(String error, int position) {
        this.error = error;
        this.position = position;
        this.value = null;
    }

    public Result(T value){
        this.value = value;
        this.error = null;
        this.position = -1;
    }

    public boolean hasError(){
        return error != null;
    }

    public boolean exists(){
        return value != null;
    }

    public @Nullable String getError() {
        return error;
    }

    public <U> Result<U> propagateError(){
        if (error == null){
            throw new NightException("Result doesn't have error");
        }
        return new Result<>(error, position);
    }

    public int getPosition() {
        return position;
    }

    public T getValue() {
        return value;
    }

    public static <T> Result<T> error(String error, int position){
        return new Result<>(error, position);
    }
}
