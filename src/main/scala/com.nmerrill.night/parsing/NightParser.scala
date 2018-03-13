package com.nmerrill.night.parsing

import com.nmerrill.night.parsing.BinaryOperator.BinaryOperator

import scala.reflect.ClassTag
import scala.util.parsing.combinator.{PackratParsers, RegexParsers}




class SimpleParsers extends RegexParsers with PackratParsers {

  private def filterByType[T](data: List[_])(implicit ev: ClassTag[T]) = data collect {
    case t: T => t
  }
  private val eol = sys.props("line.separator")
  override val skipWhitespace = false
  val whiteSpaceWithNewline: Parser[String] = whiteSpace|eol

  def ws[T](parser: Parser[T]): Parser[T] ={
    opt(whiteSpaceWithNewline)~>parser
  }
  def binary(operator: BinaryOperator, parser: Parser[String], right: Parser[Expression]): Parser[Expression => BinaryOperation] ={
    ws(parser) ~> right ^^ (r => (l: Expression) => BinaryOperation(l, operator, r))
  }

  def kw[T](parser: Parser[T]): Parser[T] ={
    ws(parser) <~ whiteSpaceWithNewline
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
       fact~(kw("*")~>literal) ^^ {case x~y => BinaryOperation(x, BinaryOperator.Multiplication, y)}
      |ws("(")~>expression<~ws(")")
      |literal
    )
  val term: PackratParser[Expression] = (
       term~(kw("+")~>fact) ^^ {case x~y => BinaryOperation(x, BinaryOperator.Addition, y)}
      |term~(kw("-")~>fact) ^^ {case x~y => BinaryOperation(x, BinaryOperator.Subtraction, y)}
      |fact
    )
  val comparison: PackratParser[Expression] = (
       comparison~(kw("lt")~>term) ^^ {case x~y => BinaryOperation(x, BinaryOperator.LessThan, y)}
      |comparison~(kw("gt")~>term) ^^ {case x~y => BinaryOperation(x, BinaryOperator.GreaterThan, y)}
      |comparison~(kw("eq")~>term) ^^ {case x~y => BinaryOperation(x, BinaryOperator.Equals, y)}
      |term
    )
  //noinspection ForwardReference
  val expression: PackratParser[Expression] = (
      expression~(ws(".")~>identifier) ^^ {case x~y => PropertyAccess(x, y)}
      |(expression<~"(")~(repsep(expression, ws(","))<~ws(")")) ^^ {case x~y => FunctionCall(x, y)}
      |_if
      |comparison
  )
  val block: PackratParser[List[Expression]] = rep(expression)
  val declaration: PackratParser[VariableDeclaration] =

  val _if: PackratParser[If] = (kw("if") ~> expression <~ ws("then")) ~ block ~ (opt(kw("else") ~> block) <~ kw("end")) ^^ {case a~b~c => If(a, b, c.getOrElse(List()))}
  val function: PackratParser[Function] = (kw("fun") ~> identifier <~ "(") ~ parameterList ~ (ws(")") ~> ws(":") ~> _type) ~ (ws("{") ~> block <~ ws("}")) ^^ {case a~b~c~d => Function(a, b, c, List(), d)}
  var _class: PackratParser[Class] = (kw("class") ~> identifier <~ "(") ~ parameterList ~ (ws(")") ~> ws("{") ~> rep(ws(function)) <~ ws("}")) ^^ {case a~b~c => Class(a, b, List(), c)}
  var source: PackratParser[SourceCode] = rep(ws(function | _class)) ^^ (a => SourceCode(filterByType[Class](a), filterByType[Function](a), List(), List()))
}

object NightParser extends SimpleParsers {
  def main(args: Array[String]): Unit = {
    parseAll(function,
      """
        |fun test(a: A):B { 1 + 2}
      """.stripMargin.trim) match {
      case Success(matched,_) => println(matched)
      case Failure(msg,_) => println("FAILURE: " + msg)
      case Error(msg,_) => println("ERROR: " + msg)
    }
  }
}
