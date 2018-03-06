grammar Token;

token: integer | string | boolean;

integer: (PLUS | MINUS)? INTEGER | ZERO;

string: 'r'? STRING;

STRING: '"' ('\\\'' | '\\\\' | ~('\'')) '"';
