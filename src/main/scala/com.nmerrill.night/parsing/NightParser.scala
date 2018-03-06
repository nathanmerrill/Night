package com.nmerrill.night.parsing

import scala.util.parsing.combinator.RegexParsers

case class LiteralInt(int: String)
case class LiteralString(string: String)
case class LiteralBoolean(bool: Boolean)

def unescapeString(string: String)

class SimpleParsers extends RegexParsers {
  def bool: Parser[LiteralBoolean]   = """true|false""".r       ^^ { a => LiteralBoolean(a.eq("true")) }
  def int: Parser[LiteralInt]    = """(-?0|[1-9]\d*)""".r ^^ { LiteralInt }
  def singleQuoteString: Parser[LiteralString] = """'.*'""".r        ^^ { a => LiteralString(a.substring(1, a.length-1)) }
  def doubleQuoteString: Parser[LiteralString] = """".*""".r        ^^ { a => LiteralString(a.substring(1, a.length-1)) }

}

object NightParser extends SimpleParsers {
  def main(args: Array[String]) = {
    parse(freq, "johnny 121") match {
      case Success(matched,_) => println(matched)
      case Failure(msg,_) => println("FAILURE: " + msg)
      case Error(msg,_) => println("ERROR: " + msg)
    }
  }
}
