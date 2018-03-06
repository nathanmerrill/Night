package com.nmerrill.night.parsing

import com.nmerrill.night.parsing.TokenType.TokenType

case class Token(tokenType: TokenType, lexeme: String, characterPosition: Int, additionalLexemes: String*)
