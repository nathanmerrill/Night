package com.nmerrill.night.parsing;

import org.checkerframework.checker.index.qual.NonNegative;

public class TextIterator {

    private int position;
    private int skip;
    private final String text;
    private final int textLength;
    public TextIterator(String text) {
        this.text = text;
        this.position = 0;
        this.skip = getSkip();
        this.textLength = text.length();
    }

    public int getPosition() {
        return position;
    }

    public String getText() {
        return text;
    }

    public int next(){
        int ret = charAt(position);
        position += skip;
        skip = getSkip();
        return ret;
    }

    private int getSkip(){
        return Character.charCount(charAt(position));
    }

    public boolean hasNext(){
        return position < textLength;
    }

    private int charAt(@NonNegative int position){
        if (position >= textLength){
            return -1;
        }
        return text.codePointAt(position);
    }

    public int peek(){
        return charAt(skip + position);
    }

    public int peek(@NonNegative int distance){
        int oldPosition = position;
        while (distance > 1){
            next();
            distance--;
        }
        int ret = peek();
        position = oldPosition;
        return ret;
    }

}
