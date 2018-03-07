package com.nmerrill.night.parsing

import com.nmerrill.night.parsing.BinaryOperator.BinaryOperator

import scala.util.parsing.combinator.{PackratParsers, RegexParsers}
import parseback._
import parseback.compat.cats._

trait Expression extends Statement{

}

trait Statement {

}

trait Literal extends Expression {

}

object Unit extends Expression

case class LiteralInt(int: String) extends Literal
case class LiteralString(string: String) extends Literal
case class LiteralBoolean(bool: Boolean) extends Literal
case class BinaryOperation(expr1: Expression, operator: BinaryOperator, expr2: Expression) extends Expression
case class PropertyAccess(expr: Expression, name: String) extends Expression
case class FunctionCall(expr: Expression, arguments: List[Expression]) extends Expression
case class If(condition: Expression, truthy: Expression, falsy: Expression) extends Expression
case class Type(_type: String)
case class VariableDeclaration(name: String, _type: Option[Type], value: Expression) extends Statement
case class Parameter(name: String, _type: Type)
case class Clause(expr: Expression)
case class Function(name: String, parameters: List[Parameter], returnType: Type, clauses: List[Clause], body: List[Statement]) extends Expression
case class Class(parameters: List[Parameter], properties: List[VariableDeclaration], functions: List[Function])
case class Import(path: String)
case class SourceCode(classes: List[Class], functions: List[Function], imports: List[Import], constants: List[VariableDeclaration])

object BinaryOperator extends Enumeration {
  type BinaryOperator = Value
  var Addition, Subtraction, Multiplication, Equals, GreaterThan, LessThan = Value
}

class ParsebackParser {
  val parseback = {

    lazy val expr: Parser[Int] = (
      expr ~ "+" ~ term ^^ { (_, e, _, t) => e + t }
        | expr ~ "-" ~ term ^^ { (_, e, _, t) => e - t }
        | term
      )

    lazy val term: Parser[Int] = (
      term ~ "*" ~ factor ^^ { (_, e, _, f) => e * f }
        | term ~ "/" ~ factor ^^ { (_, e, _, f) => e / f }
        | factor
      )

    lazy val factor: Parser[Int] = (
      "(" ~> expr <~ ")"
        | "-" ~ factor       ^^ { (_, _, e) => -e }
        | """\d+""".r        ^^ { (_, str) => str.toInt }
      )

    expr
  }
}



class SimpleParsers {
  implicit val W: Whitespace = Whitespace("""\s+"""r)


  val parseback = {
    lazy val expr: Parser[Expression] = (
      expr ~ "+" ~ term ^^ { (loc, e, _, t) => BinaryOperation(e, BinaryOperator.Addition, t) }
        | expr ~ "-" ~ term ^^ { (loc, e, _, t) => BinaryOperation(e, BinaryOperator.Subtraction, t) }
        | term
      )

    lazy val term: Parser[Expression] = (
      expr ~ "*" ~ factor ^^ { (loc, e, _, f) => BinaryOperation(e, BinaryOperator.Multiplication, f) }
        | factor
      )

    lazy val factor: Parser[Expression] = (
      "(" ~> expr <~ ")"
        |
        """\d+""".r ^^ { (loc, str) => LiteralInt(str) }
      )
  }
//  def bool: Parser[LiteralBoolean]   = """true|false""".r           ^^ { a => LiteralBoolean(a.eq("true")) }
//  def int: Parser[LiteralInt]    = """(0|-?[1-9]\d*)""".r           ^^ { LiteralInt }
//  def singleQuoteString: Parser[LiteralString] = """'[^\']*'""".r   ^^ { a => LiteralString(a.substring(1, a.length-1)) }
//  def doubleQuoteString: Parser[LiteralString] = """"[^\"]*"""".r   ^^ { a => LiteralString(a.substring(1, a.length-1)) }
//  def lessThan: Parser[BinaryOperator] = "lt"                       ^^ { _ => BinaryOperator.LessThan}
//  def greaterThan: Parser[BinaryOperator] = "gt"                    ^^ { _ => BinaryOperator.GreaterThan}
//  def equals: Parser[BinaryOperator] = "eq"                         ^^ { _ => BinaryOperator.Equals}
//  def addition: Parser[BinaryOperator] = "+"                        ^^ { _ => BinaryOperator.Addition}
//  def subtraction: Parser[BinaryOperator] = "-"                     ^^ { _ => BinaryOperator.Subtraction}
//  def multiplication: Parser[BinaryOperator] = "*"                  ^^ { _ => BinaryOperator.Multiplication}
//
//  lazy val binaryOperator: PackratParser[BinaryOperator] = lessThan | greaterThan | equals | addition | subtraction | multiplication
//  lazy val binaryOperation: PackratParser[BinaryOperation] = expression ~ binaryOperator ~ expression ^^ { case e1 ~ op ~ e2 => BinaryOperation(e1, op, e2)}
//  lazy val string: PackratParser[LiteralString] = singleQuoteString | doubleQuoteString
//  lazy val literal: PackratParser[Literal] = bool | int | string
//  lazy val expression: PackratParser[Expression] = binaryOperation | literal

}

object NightParser extends SimpleParsers {
  def main(args: Array[String]): Unit = {
    parseback(LineStream("\"test\" eq 1 eq false") match {
      case Success(matched,_) => println(matched)
      case Failure(msg,_) => println("FAILURE: " + msg)
      case Error(msg,_) => println("ERROR: " + msg)
    }
  }
}
