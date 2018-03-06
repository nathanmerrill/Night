grammar Night;

import Expression;

@header {
package com.nmerrill.elegance.antlr;
}

file: (ele_package)? (file_import)* (ele_class | function | statement | ele_enum)* EOF;

ele_package :
    PACKAGE_DECLARATION module=IDENTIFIER COLON package_path;

package_path :
    IDENTIFIER (PERIOD IDENTIFIER)*;

file_import :
    IMPORT_DECLARATION (module=IDENTIFIER COLON)? package_path;

ele_class :
    CLASS IDENTIFIER
    type_parameters?
    parameter_list?
    type_alias*
    OPEN_CURLY
    (function | assignment)*
    CLOSE_CURLY;

function:
    FUNCTION_DECLARATION IMPURE_DECLARATION? IDENTIFIER parameter_list
    COLON type
    (MUTABLE_DECLARATION OPEN_PAREN (variable_reference (COMMA variable_reference)*)? CLOSE_PAREN)?
    (
        OPEN_CURLY (function | statement)* CLOSE_CURLY |
        EQUAL expression SEMICOLON
    );

ele_enum:
    ENUM_DECLARATION name=IDENTIFIER OPEN_CURLY (IDENTIFIER (COMMA IDENTIFIER)*)? CLOSE_CURLY ;

assignment:
    (VALUE_DECLARATION | VARIABLE_DECLARATION | OPTION_DECLARATION)?
    (MUTABLE_DECLARATION)?
    variable_reference
    (COLON type)?
    (EQUAL expression)?
    SEMICOLON;

statement:
    (assignment | statement_if | ele_for | RETURN_DECLARATION? expression SEMICOLON);

statement_if: IF expression DO statement* (ELSE statement*)? END;
ele_for: FOR_LOOP IDENTIFIER IN expression DO statement* END;



parameter_list:
    OPEN_PAREN
        (parameter
            (COMMA  parameter)*
        )?
    CLOSE_PAREN;

parameter:
    MUTABLE_DECLARATION? expression
    (COLON type)?;

type:
    type ASTERISK |  //Iterable type
    type QUESTION_MARK |  //Optional type
    OPEN_PAREN type (COMMA type)* CLOSE_PAREN ARROW type | //Function type
    type ARROW type | //Function type
    type (COMMA type)+ |  //Pair type
    OPEN_PAREN type CLOSE_PAREN |
    IDENTIFIER
        type_parameters?
        (OPEN_BRACKET trait_expression CLOSE_BRACKET)?;


type_parameters:
    (OPEN_ANGLE_BRACKET type (SEMICOLON type)* CLOSE_ANGLE_BRACKET);

trait_expression:
    trait_expression OR trait_expression |
    trait_expression AND trait_expression |
    IDENTIFIER (OPEN_PAREN (literal (COMMA literal)*)? CLOSE_PAREN)?;

type_alias:
    WHERE_DECLARATION IDENTIFIER COLON type;





INTEGER: [1-9][0-9]* ;

FLOAT: ('0' | [1-9][0-9]* ) '.' [0-9]+;

ZERO: '0' ;

STRING: '"' ('\\'. | ~('"' | '\\'))* '"' |  '\'' ('\\'. | ~('\'' | '\\'))* '\'';

SET_START: 's[';
TUPLE_START: 't[';
DICT_START: 'd[';

PACKAGE_DECLARATION: 'package' ;
CLASS : 'class' ;
FUNCTION_DECLARATION: 'fun' ;
VALUE_DECLARATION: 'val' ;
VARIABLE_DECLARATION: 'var' ;
OPTION_DECLARATION: 'option' ;
IMPORT_DECLARATION: 'import' ;
WHERE_DECLARATION: 'where' ;
MUTABLE_DECLARATION: 'mut' ;
ENUM_DECLARATION: 'enum' ;
IMPURE_DECLARATION: 'impure' ;
RETURN_DECLARATION: 'return' ;

OPEN_PAREN: '(' ;
CLOSE_PAREN: ')';
OPEN_ANGLE_BRACKET: '<' ;
CLOSE_ANGLE_BRACKET: '>';
OPEN_BRACKET: '[' ;
CLOSE_BRACKET: ']' ;

IF: 'if' ;
ELSE: 'else';
FOR_LOOP: 'for' ;
IN: 'in' ;
DO: 'do' ;
END: 'end';


COLON: ':' ;
COMMA: ',';

SEMICOLON: ';';

UNDERSCORE: '_';

ARROW : '->';

QUESTION_MARK: '?';

COMMENT: ('//' ~('\r' | '\n')+) -> skip;
WS: ('\n' | '\r' | '\t' | ' ')+ -> skip;
