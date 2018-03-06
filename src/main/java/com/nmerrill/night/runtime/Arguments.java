package com.nmerrill.night.runtime;


import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

public class Arguments {
    @Option(names = { "-v", "--verbose" }, description = "Be verbose.")
    public boolean verbose = false;

    @Parameters(index="0", paramLabel = "command", description = "Which operation we should perform", arity = "0")
    public Command command;
}
