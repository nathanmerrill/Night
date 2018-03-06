package com.nmerrill.night.parsing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.nmerrill.night.Result;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.dataflow.qual.Pure;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.nmerrill.night.parsing.Codepoints.*;
import static com.nmerrill.night.parsing.TokenType.*;


public final class NightScanner implements Iterator<Result<Token>> {

    public static Iterable<Result<Token>> iterable(String text) {
        return () -> new NightScanner(new TextIterator(text));
    }

    public static ScanResults scan(String text) {
        ImmutableList.Builder<ScanError> errors = ImmutableList.builder();
        ImmutableList.Builder<Token> tokens = ImmutableList.builder();
        for (Result<Token> token : iterable(text)) {
            String error = token.getError();
            if (error != null){
                errors.add(new ScanError(error, token.getPosition()));
            } else {
                Token t = token.getValue();
                assert t != null;
                tokens.add(t);
            }
        }
        return new ScanResults(tokens.build(), errors.build());
    }

    private final TextIterator iterator;
    private static final ImmutableMap<Integer, TokenType> BASIC_TOKEN_MAP = ImmutableMap.<Integer, TokenType>builder()
            .put(LEFT_PARENTHESIS, LEFT_PAREN)
            .put(RIGHT_PARENTHESIS, RIGHT_PAREN)
            .put(LEFT_CURLY_BRACKET, LEFT_BRACE)
            .put(RIGHT_CURLY_BRACKET, RIGHT_BRACE)
            .put(LEFT_SQUARE_BRACKET, LEFT_BRACKET)
            .put(RIGHT_SQUARE_BRACKET, RIGHT_BRACKET)
            .put(LESS_THAN_SIGN, LEFT_ANGLE)
            .put(GREATER_THAN_SIGN, RIGHT_ANGLE)
            .put(Codepoints.COMMA, TokenType.COMMA)
            .put(PLUS_SIGN, PLUS)
            .put(Codepoints.SEMICOLON, TokenType.SEMICOLON)
            .put(SOLIDUS, SLASH)
            .put(Codepoints.ASTERISK, TokenType.ASTERISK)
            .put(QUESTION_MARK, QUESTION)
            .put(VERTICAL_LINE, BAR)
            .put(Codepoints.AMPERSAND, TokenType.AMPERSAND)
            .put(EQUALS_SIGN, ASSIGNMENT)
            .put(FULL_STOP, DOT)
            .put(HYPHEN_MINUS, MINUS)
            .build();

    private static final ImmutableSet NEW_LINES =
            ImmutableSet.of(Codepoints.LINE_FEED, Codepoints.CARRIAGE_RETURN);

    private static final ImmutableSet STRING_STARTS =
            ImmutableSet.of(Codepoints.QUOTATION_MARK, Codepoints.APOSTROPHE, Codepoints.GRAVE_ACCENT);


    public NightScanner(TextIterator iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Result<Token> next() {
        Result<Token> token = buildToken();
        iterator.next();
        return token;
    }

    private Result<Token> buildToken() {
        int position = iterator.getPosition();
        int current;
        while (true) {
            current = iterator.peek();
            if (!Character.isValidCodePoint(current)) {
                return Result.error("Invalid code point: " + toHex(current), position);
            }
            if (!Character.isWhitespace(current)) {
                break;
            }
            iterator.next();
        }
        int next = iterator.peek(2);
        String character = new String(Character.toChars(current));
        if (STRING_STARTS.contains(current)) {
            return matchString();
        }

        if (Character.isDigit(current) || (current == HYPHEN_MINUS && Character.isDigit(next))) {
            return matchNumber();
        }
        if (current == FULL_STOP && next == QUESTION_MARK) {
            return new Result<>(new Token(DOT_QUESTION, ".?", position));
        }
        if (current == HYPHEN_MINUS && next == GREATER_THAN_SIGN) {
            return new Result<>(new Token(ARROW, "->", position));
        }
        TokenType type = BASIC_TOKEN_MAP.get(current);
        if (type != null) {
            return new Result<>(new Token(type, character, position));
        }
        if (Character.isUnicodeIdentifierStart(current) || current == Codepoints.LOW_LINE) {
            return matchIdentifier();
        }
        return Result.error("Unrecognized symbol: " + toHex(current), position);
    }

    @Pure
    private String toHex(int codePoint) {
        return "0x" + Integer.toHexString(codePoint);
    }

    private Result<String> buildSequence
            (Predicate<Integer> stop, Predicate<Integer> allowed, Function<Integer, String> error) {
        StringBuilder stringBuilder = new StringBuilder();
        while (iterator.hasNext()) {
            Integer next = iterator.peek();
            if (stop.test(next)) {
                break;
            }
            iterator.next();
            stringBuilder.appendCodePoint(next);
            if (!allowed.test(next)) {
                return Result.error(error.apply(next), iterator.getPosition());
            }
        }
        return new Result<>(stringBuilder.toString());
    }

    private Result<Token> matchString() {
        int startPosition = iterator.getPosition() + 1;
        Integer delimeter = iterator.next();
        boolean allowNewlines = delimeter == Codepoints.GRAVE_ACCENT;
        Result<String> string;
        if (!allowNewlines) {
            string = buildSequence(delimeter::equals, s -> !NEW_LINES.contains(s), s ->
                    "Strings cannot contain newlines unless they use a backtick as the delimiter"
            );

        } else {
            string = buildSequence(delimeter::equals, s -> true, s -> "");
        }
        if (string.hasError()) {
            return string.propagateError();
        }
        return new Result<>(new Token(TokenType.STRING, string.getValue(), startPosition));
    }


    private Result<Token> matchIdentifier() {
        int position = iterator.getPosition();
        StringBuilder identifier = new StringBuilder();
        identifier.appendCodePoint(iterator.next());
        int current = iterator.peek();
        while (true) {
            if (current == Codepoints.END_OF_TEXT || Character.isWhitespace(current)) {
                break;
            }
            if (Character.isUnicodeIdentifierPart(current)) {
                identifier.appendCodePoint(current);
            } else if (!Character.isIdentifierIgnorable(current)) {
                break;
            }
            iterator.next();
            current = iterator.peek();
        }
        TokenType tokenType = TokenType.IDENTIFIER;
        @MonotonicNonNull String secondLexeme = null;
        if (current == LEFT_SQUARE_BRACKET) {
            tokenType = TokenType.LEFT_BRACKET_PREFIX;
        }
        if (STRING_STARTS.contains(current)) {
            tokenType = TokenType.STRING_PREFIX;
            Result<Token> string = matchString();
            if (string.hasError()) {
                return string.propagateError();
            }
            secondLexeme = string.getValue().getSecondLexeme();
        }
        return new Result<>(new Token(tokenType, identifier.toString(), secondLexeme, position));
    }

    private Result<Token> matchNumber() {
        StringBuilder numberBuilder = new StringBuilder();
        numberBuilder.appendCodePoint(iterator.next());
        int position = iterator.getPosition();
        boolean seenDecimal = false;
        while (true) {
            int current = iterator.peek();
            int next = iterator.peek(2);
            if (current == Codepoints.LOW_LINE) {
                if (Character.isDigit(next)) {
                    iterator.next(); //Ignoring _ so numbers can be readable
                    continue;
                }
            }
            if (current == Codepoints.FULL_STOP) {
                if (seenDecimal) {
                    return Result.error("Cannot have two decimal points in a number", position);
                }
                if (!Character.isDigit(next)) {
                    return Result.error("Number cannot end with a decimal point", position);
                }
                seenDecimal = true;
                numberBuilder.appendCodePoint(current);
                iterator.next();
                continue;
            }
            if (!Character.isDigit(current)) {
                break;
            }
            numberBuilder.appendCodePoint(current);
            iterator.next();
        }
        TokenType tokenType = seenDecimal ? DECIMAL : INTEGER;
        @MonotonicNonNull String secondLexeme = null;
        if (Character.isUnicodeIdentifierStart(iterator.peek())) {
            Result<Token> identifier = matchIdentifier();
            if (identifier.hasError()) {
                return identifier.propagateError();
            } else {
                secondLexeme = identifier.getValue().getLexeme();
                tokenType = tokenType == DECIMAL ? DECIMAL_SUFFIX : INTEGER_SUFFIX;
            }
        }
        return new Result<>(new Token(tokenType, numberBuilder.toString(), secondLexeme, position));
    }

}
