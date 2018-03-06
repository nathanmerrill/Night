package com.nmerrill.night.runtime;

import com.nmerrill.night.Context;
import com.nmerrill.night.Value;
import com.nmerrill.night.parsing.NightScanner;
import com.nmerrill.night.parsing.Parser;
import com.nmerrill.night.parsing.TextIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;


public class Repl {
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Context context;
    private final BufferedReader reader;
    private final Parser parser;

    public Repl(Arguments arguments) {
        this.context = new Context();
        InputStreamReader input = new InputStreamReader(System.in);
        parser = new Parser();
        reader = new BufferedReader(input);
    }

    public void run(){
        while (true){
            String input;
            try {
                input = read();
            } catch (IOException e){
                System.out.print("Unable to read input!");
                return;
            }
            Value value = evaluate(input);
            print(value);
        }
    }

    public String read() throws IOException {
        //TODO:  custom shell with auto-complete
        System.out.print("night>");
        String input = "";
        while (input.isEmpty()){
            input = reader.readLine().trim();
        }
        return input;

    }

    public Value evaluate(String code){
        NightScanner scanner = new NightScanner(new TextIterator(code));

        return null;
    }

    public void print(Value value){

    }
}
