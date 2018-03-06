grammar Expression;

expression:
        expression argument_list | //Function call
        expression OR expression |
        expression AND expression |
        expression (IS | EQ | NEQ)  expression |
        expression (LEQ | GEQ | LT | GT)  expression |
        expression (PLUS | MINUS)  expression |
        expression (ASTERISK | DIV)  expression |
        expression PERIOD  IDENTIFIER |
        expression OPEN_BRACKET expression CLOSE_BRACKET |
        expression_if |
        OPEN_PAREN  expression  CLOSE_PAREN |
        literal
        variable_reference;

variable_reference: IDENTIFIER;

argument_list:
    OPEN_PAREN
       (argument
           (COMMA  argument)*
           (COMMA  named_argument)*
       | named_argument
           (COMMA  named_argument)*
       )?
   CLOSE_PAREN;

literal: integer | ele_float | list | tuple | dictionary | set | STRING ;

list: OPEN_BRACKET  (expression (COMMA  expression)* )? CLOSE_BRACKET ;

tuple: TUPLE_START  (expression (COMMA  expression)* )? CLOSE_BRACKET ;

set: SET_START  (expression (COMMA  expression)* )? CLOSE_BRACKET ;

dictionary: DICT_START  (expression COLON expression (COMMA  expression COLON expression)* )? CLOSE_BRACKET ;

integer: (PLUS | MINUS)? INTEGER | ZERO;

ele_float: (PLUS | MINUS)? FLOAT;

argument: expression;

named_argument: IDENTIFIER EQUAL expression;

OR: 'or';
AND: 'and';
IS: 'is';
EQ: 'eq';
NEQ: 'neq';
LEQ: 'leq';
GEQ: 'geq';
LT: 'lt';
GT: 'gt';


EQUAL: '=';

ASTERISK: '*';
PLUS: '+';
MINUS: '-';
DIV: '/';


PERIOD: '.' ;

IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]*;

OPEN_CURLY: '{' ;
CLOSE_CURLY: '}' ;