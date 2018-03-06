package com.nmerrill.night.parsing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class ParseResults {
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ParseTree tree;
    private final List<ParseError> errors;
    private ParseResults(ParseTree tree, List<ParseError> parseErrors) {
        this.tree = tree;
        this.errors = new ArrayList<>();
    }

    public boolean successful(){
        return this.tree != null;
    }

    public ParseTree getTree() {
        return tree;
    }

    public List<ParseError> getErrors() {
        return errors;
    }

    public static ParseResults success(ParseTree parseTree){
        return new ParseResults(parseTree, new ArrayList<>());
    }

    public static ParseResults error(List<ParseError> errors){
        return new ParseResults(null, errors);
    }
}
