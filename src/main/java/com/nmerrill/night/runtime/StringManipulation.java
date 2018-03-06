package com.nmerrill.night.runtime;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class StringManipulation {
    private StringManipulation(){}

    public static <T> String join(T[] collection) {
        return join(collection, ",");
    }

    public static <T> String join(T[] collection, String delimiter) {
        return join(collection, delimiter, Object::toString);
    }

    public static <T> String join(T[] collection, String delimiter, Function<T, String> serializer)
    {
        return join(Arrays.stream(collection), delimiter, serializer);
    }

    public static <T> String join(Collection<T> collection) {
        return join(collection, ",");
    }

    public static <T> String join(Collection<T> collection, String delimiter) {
        return join(collection, delimiter, Object::toString);
    }

    public static <T> String join(Collection<T> collection, String delimiter, Function<T, String> serializer)
    {
        return join(collection.stream(), delimiter, serializer);
    }

    private static <T> String join(Stream<T> stream, String delimiter, Function<T, String> serializer){

        return stream.map( serializer )
                .collect( Collectors.joining( delimiter ) );
    }
}
