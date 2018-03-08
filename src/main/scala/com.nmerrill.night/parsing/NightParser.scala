package com.nmerrill.night.parsing

import com.nmerrill.night.parsing.BinaryOperator.BinaryOperator

import scala.reflect.ClassTag
import scala.util.matching.Regex
import scala.util.parsing.combinator.{PackratParsers, RegexParsers}

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
case class If(condition: Expression, truthy: List[Statement], falsy: List[Statement]) extends Expression
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



class SimpleParsers extends RegexParsers with PackratParsers {

  private val eol = sys.props("line.separator")
  override val skipWhitespace = false
  val wsParser: Parser[String] = whiteSpace|eol

  def ws[T](parser: Parser[T]): Parser[T] ={
    opt(wsParser)~>parser
  }
  def binary(operator: BinaryOperator, parser: Parser[String], right: Parser[Expression]): Parser[Expression => BinaryOperation] ={
    ws(parser) ~> right ^^ (r => (l: Expression) => BinaryOperation(l, operator, r))
  }

  def bool: Parser[LiteralBoolean]   = """true|false""".r       ^^ { a => LiteralBoolean(a.eq("true")) }
  def int: Parser[LiteralInt]    = """(0|-?[1-9]\d*)""".r       ^^ LiteralInt
  def singleQuoteString: Parser[LiteralString] = """'[^\']*'""".r   ^^ { a => LiteralString(a.substring(1, a.length-1)) }
  def doubleQuoteString: Parser[LiteralString] = """"[^\"]*"""".r   ^^ { a => LiteralString(a.substring(1, a.length-1)) }
  def identifier: Parser[String] = ws("""[_a-zA-Z][_a-zA-Z0-9]*""".r)

  def string: Parser[LiteralString] = singleQuoteString | doubleQuoteString
  def literal: Parser[Literal] = ws(bool | int | string)

  def _type: Parser[Type] = identifier ^^ Type

  def parameter: Parser[Parameter] = identifier ~ (ws(":") ~> _type) ^^ { case a ~ b => Parameter(a, b) }

  def parameterList: Parser[List[Parameter]] = repsep(parameter, ws(","))

  //noinspection ForwardReference
  val fact: PackratParser[Expression] = (
       fact~ws("*"~> wsParser ~>literal) ^^ {case x~y => BinaryOperation(x, BinaryOperator.Multiplication, y)}
      |ws("(")~>expression<~ws(")")
      |literal
    )
  val term: PackratParser[Expression] = (
       term~ws("+" ~> wsParser ~>fact) ^^ {case x~y => BinaryOperation(x, BinaryOperator.Addition, y)}
      |term~ws("-" ~> wsParser ~> fact) ^^ {case x~y => BinaryOperation(x, BinaryOperator.Subtraction, y)}
      |fact
    )
  val comparison: PackratParser[Expression] = (
       comparison~(ws("lt") ~> wsParser ~>term) ^^ {case x~y => BinaryOperation(x, BinaryOperator.LessThan, y)}
      |comparison~(ws("gt") ~> wsParser ~>term) ^^ {case x~y => BinaryOperation(x, BinaryOperator.GreaterThan, y)}
      |comparison~(ws("eq") ~> wsParser ~>term) ^^ {case x~y => BinaryOperation(x, BinaryOperator.Equals, y)}
      |term
    )
  //noinspection ForwardReference
  val expression: PackratParser[Expression] = (
      expression~("."~>identifier) ^^ {case x~y => PropertyAccess(x, y)}
      |(expression<~"(")~(repsep(expression, ws(","))<~ws(")")) ^^ {case x~y => FunctionCall(x, y)}
      |_if
      |comparison
  )
  val block: PackratParser[List[Expression]] = rep(expression)

  val _if: PackratParser[If] = (ws("if") ~> expression <~ ws("then")) ~ block ~ (opt(ws("else") ~> block) <~ ws("end")) ^^ {case a~b~c => If(a, b, c.getOrElse(List()))}
  val function: PackratParser[Function] = (ws("fun") ~> identifier <~ "(") ~ parameterList ~ (ws(")") ~> ws(":") ~> _type) ~ ("{" ~> block <~ "}") ^^ {case a~b~c~d => Function(a, b, c, List(), d)}

}

object NightParser extends SimpleParsers {
  def main(args: Array[String]): Unit = {
    parseAll(expression,
      """
        |if 1 + -2 then
        | 3 + 4
        |end
      """.stripMargin.trim) match {
      case Success(matched,_) => println(matched)
      case Failure(msg,_) => println("FAILURE: " + msg)
      case Error(msg,_) => println("ERROR: " + msg)
    }
  }
}
