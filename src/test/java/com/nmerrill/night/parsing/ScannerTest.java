package com.nmerrill.night.parsing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ScannerTest {
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());



    @Test
    public void emptyString() {
        ScanResults results = NightScanner.scan("");
        assertNoErrors(results);
        Assert.assertTrue(results.getTokens().isEmpty());
    }

    @DataProvider(name = "integers")
    public Object[][] integerData() {
        return new Object[][]{
                {"0"},
                {"2"},
                {"-4"},
                {"100"},
                {"400000000000000000000000000"},
                {"-400000000000000000000000000"},
                {"1_000_000"},
        };
    }


    @Test(dataProvider = "integers")
    public void numbers(String number) {
        ScanResults results = NightScanner.scan(number);
        String expected = number.replaceAll("_", "");
        assertTokens(results, expected);
        assertTokenTypes(results, TokenType.INTEGER);
    }

    @DataProvider(name = "floats")
    public Object[][] floatData() {
        return new Object[][]{
                {"4.5"},
                {"-4.5"},
                {"0.5"},
                {"1_000.5"},
                {"1.0_5"},
                {"-1_0.5"},
        };
    }


    @Test(dataProvider = "floats")
    public void floats(String number) {
        ScanResults results = NightScanner.scan(number);
        String expected = number.replaceAll("_", "");
        assertTokens(results, expected);
        assertTokenTypes(results, TokenType.DECIMAL);
    }

    @DataProvider(name = "invalidNumbers")
    public Object[][] invalidNumberData() {
        return new Object[][]{
                {"0a"},
                {"4b0"},
                {"455x"},
                {"1..23"},
                {"3.6.7"},
                {"-3.4.800"},
                {"2."},
                {"-55t"},
        };
    }

    @Test(dataProvider = "invalidNumbers")
    public void invalidNumbers(String invalid) {
        ScanResults results = NightScanner.scan(invalid);
        assertError(results);
    }

    @DataProvider(name = "identifiers")
    public Object[][] identifierData(){
        return new Object[][]{
                {"a"},
                {"abc"},
                {"camelCase"},
                {"snake_case"},
                {"TitleCase"},
                {"ALL_CAPS"},
                {"withNumber99"},
                {"_underscore"},
                {"_"},
                {"Español"},
                {"français"},
                {"日本語"},
                {"中文"},
                {"हिंदी"},
                {"русский"},
                {"عربى"},
        };
    }

    @Test(dataProvider = "identifiers")
    public void identifiers(String identifier){
        ScanResults results = NightScanner.scan(identifier);
        assertTokenTypes(results, TokenType.IDENTIFIER);
        assertTokens(results, identifier);
    }

    @DataProvider(name = "ignorables")
    public Object[][] ignorableData(){
        return new Object[][]{
                {"a\u0000\u0001\u0002\u0004\u0005\u0006\u0007\u0008b"},// Intentionally skip \u0003
                {"a\u000E\u000F\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001A\u001Bb"},
                {"a\u007Fb"},
                {"a\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089\u008A\u008B\u008C\u008D\u008E\u008Fb"},
                {"a\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097\u0098\u0099\u009A\u009B\u009C\u009D\u009E\u009Fb"},
        };
    }

    @Test(dataProvider = "ignorables")
    public void identifierIgnores(String ignorable){
        ScanResults results = NightScanner.scan(ignorable);
        assertTokenTypes(results, TokenType.IDENTIFIER);
        assertTokens(results, "ab");
    }

    @DataProvider(name="strings")
    public Iterator<Object[]> stringData(){
        List<Object[]> data = new ArrayList<>();
        String[] literals = new String[] {
                "",
                "abc",
                "Español",
                "français",
                "日本語",
                "中文",
                "हिंदी",
                "русский",
                "عربى",
        };
        for (String literal : literals) {
            data.add(new Object[]{
                    "\"" + literal + "\"", literal
            });
            data.add(new Object[]{
                    "`" + literal + "`", literal
            });
        }
        data.add(new Object[]{"\"`\"", "`"});
        data.add(new Object[]{"`\"`", "\""});
        String[][] escapes = new String[][]{
                {"\\n", "\n"},
                {"\\\\", "\\"},
                {"\\u0035", "5"},
                {"\\U0001D49E", "\uD835\uDC9E"},
                {"\\\"", "\""},
                {"\\'", "'"}
        };
        for (String[] escape: escapes){
           data.add(new Object[]{"\""+escape[0]+"\"", escape[1]});
        }
        for (String[] escape: escapes){
            data.add(new Object[]{"`"+escape[1]+"`", escape[1]});
        }
        data.add(new Object[]{"\"\\r\"", "\r"});
        data.add(new Object[]{"`\r`", ""});
        return data.iterator();
    }

    @Test(dataProvider = "strings")
    public void strings(String string, String match){
        ScanResults results = NightScanner.scan(string);
        assertTokenTypes(results, TokenType.STRING);
        assertTokens(results, match);
    }


    private void assertTokens(ScanResults results, String... tokens) {
        assertTokensLength(results, tokens.length);
        for (int i = 0; i < tokens.length; i++) {
            Assert.assertEquals(results.getTokens().get(i).getLexeme(), tokens[i], "Wrong token contents");
        }
    }

    private void assertTokenTypes(ScanResults results, TokenType... tokens) {
        assertTokensLength(results, tokens.length);
        for (int i = 0; i < tokens.length; i++) {
            Assert.assertEquals(results.getTokens().get(i).getTokenType(), tokens[i], "Wrong token type");
        }
    }

    private void assertTokensLength(ScanResults results, int length) {
        assertNoErrors(results);
        Assert.assertEquals(results.getTokens().size(), length, "Wrong number of tokens returned");
    }

    private void assertNoErrors(ScanResults scanResults) {
        Assert.assertTrue(scanResults.getErrors().isEmpty(), "Scanner errors:" + scanResults.getErrors().stream().map(ScanError::getMessage).collect(Collectors.joining()));
    }

    private void assertError(ScanResults scanResults) {
        assertErrors(scanResults, 1);
    }

    private void assertErrors(ScanResults scanResults, int count) {
        Assert.assertFalse(scanResults.isSuccess(), "No errors returned");
        Assert.assertEquals(scanResults.getErrors().size(), count, "Wrong number of errors returned");
    }

}
