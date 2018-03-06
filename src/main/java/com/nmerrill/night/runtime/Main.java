package com.nmerrill.night.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.lang.invoke.MethodHandles;

public class Main {
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static void main(String[] args){
        Arguments arguments = CommandLine.populateCommand(new Arguments(), args);
        switch (arguments.command){
            case REPL:
                new Repl(arguments).run();
        }
    }
}
