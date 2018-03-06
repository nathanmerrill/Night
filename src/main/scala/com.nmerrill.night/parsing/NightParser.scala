package com.nmerrill.night.parsing

import scala.util.parsing.combinator.RegexParsers

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
case class BinaryOperation(expr1: Expression, operator: BinaryOperation, expr2: Expression) extends Expression
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


class SimpleParsers extends RegexParsers {
  def bool: Parser[LiteralBoolean]   = """true|false""".r       ^^ { a => LiteralBoolean(a.eq("true")) }
  def int: Parser[LiteralInt]    = """(-?0|[1-9]\d*)""".r       ^^ { LiteralInt }
  def singleQuoteString: Parser[LiteralString] = """'.*'""".r   ^^ { a => LiteralString(a.substring(1, a.length-1)) }
  def doubleQuoteString: Parser[LiteralString] = """".*""".r    ^^ { a => LiteralString(a.substring(1, a.length-1)) }
  def string: Parser[LiteralString] = singleQuoteString | doubleQuoteString
  def literal: Parser[Literal] = bool | int | string
}

object NightParser extends SimpleParsers {
  def main(args: Array[String]): Unit = {
    parse(literal, "\"test\"") match {
      case Success(matched,_) => println(matched)
      case Failure(msg,_) => println("FAILURE: " + msg)
      case Error(msg,_) => println("ERROR: " + msg)
    }
  }
}
