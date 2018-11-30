package com.nmerrill.night.parsing
import fastparse.Parsed.Success
import fastparse._
import scalaz.std.{function, int}

import scala.reflect.ClassTag


class SimpleParsers {
  def parseBool[_: P] = P("false" | "true")

//class SimpleParsers extends RegexParsers with PackratParsers {
//
//  private def filterByType[T](data: List[_])(implicit ev: ClassTag[T]) = data collect {
//    case t: T => t
//  }
//  private val eol = sys.props("line.separator").toString
//  override val skipWhitespace = false
//  val whiteSpaceWithNewline: Parser[String] = whiteSpace|eol
//
//  def ws[T](parser: Parser[T]): Parser[T] ={
//    opt(whiteSpaceWithNewline)~>parser
//  }
//  def binary(operator: BinaryOperator, parser: Parser[String], right: Parser[Expression]): Parser[Expression => BinaryOperation] ={
//    ws(parser) ~> right ^^ (r => (l: Expression) => BinaryOperation(l, operator, r))
//  }
//
//  def kw[T](parser: Parser[T]): Parser[T] ={
//    ws(parser) <~ whiteSpaceWithNewline
//  }
//
//  def bool: Parser[LiteralBoolean]   = """true|false""".r       ^^ { a => LiteralBoolean(a.eq("true")) }
//  def int: Parser[LiteralInt]    = """(0|-?[1-9]\d*)""".r       ^^ LiteralInt
//  def singleQuoteString: Parser[LiteralString] = """'[^\']*'""".r   ^^ { a => LiteralString(a.substring(1, a.length-1)) }
//  def doubleQuoteString: Parser[LiteralString] = """"[^\"]*"""".r   ^^ { a => LiteralString(a.substring(1, a.length-1)) }
//  def identifier: Parser[String] = ws("""[_a-zA-Z][_a-zA-Z0-9]*""".r)
//
//  def string: Parser[LiteralString] = singleQuoteString | doubleQuoteString
//  def literal: Parser[Literal] = ws(bool | int | string)
//
//  def _type: Parser[Type] = identifier ^^ Type
//
//  def parameter: Parser[Parameter] = identifier ~ (ws(":") ~> _type) ^^ { case a ~ b => Parameter(a, b, List()) }
//
//  def parameterList: Parser[List[Parameter]] = repsep(parameter, ws(","))
//
//  //noinspection ForwardReference
//  val fact: PackratParser[Expression] = (
//       fact~(kw("*")~>literal) ^^ {case x~y => BinaryOperation(x, BinaryOperator.Multiplication, y)}
//      |ws("(")~>expression<~ws(")")
//      |literal
//    )
//  val term: PackratParser[Expression] = (
//       term~(kw("+")~>fact) ^^ {case x~y => BinaryOperation(x, BinaryOperator.Addition, y)}
//      |term~(kw("-")~>fact) ^^ {case x~y => BinaryOperation(x, BinaryOperator.Subtraction, y)}
//      |fact
//    )
//  val comparison: PackratParser[Expression] = (
//       comparison~(kw("lt")~>term) ^^ {case x~y => BinaryOperation(x, BinaryOperator.LessThan, y)}
//      |comparison~(kw("gt")~>term) ^^ {case x~y => BinaryOperation(x, BinaryOperator.GreaterThan, y)}
//      |comparison~(kw("eq")~>term) ^^ {case x~y => BinaryOperation(x, BinaryOperator.Equals, y)}
//      |term
//    )
//  //noinspection ForwardReference
//  val expression: PackratParser[Expression] = (
//      expression~(ws(".")~>identifier) ^^ {case x~y => PropertyAccess(x, y)}
//      |(expression<~"(")~(repsep(expression, ws(","))<~ws(")")) ^^ {case x~y => FunctionCall(x, y)}
//      |_if
//      |comparison
//  )
//  val block: PackratParser[List[Expression]] = rep(expression)
//
//  val _if: PackratParser[If] = (kw("if") ~> expression <~ ws("then")) ~ block ~ (opt(kw("else") ~> block) <~ kw("end")) ^^ {case a~b~c => If(a, b, c.getOrElse(List()))}
//  val function: PackratParser[Function] = (kw("fun") ~> identifier <~ "(") ~ parameterList ~ (ws(")") ~> ws(":") ~> _type) ~ (ws("{") ~> block <~ ws("}")) ^^ {case a~b~c~d => Function(a, b, c, List(), d)}
//  var _class: PackratParser[Class] = (kw("class") ~> identifier <~ "(") ~ parameterList ~ (ws(")") ~> ws("{") ~> rep(ws(function)) <~ ws("}")) ^^ {case a~b~c => Class(a, b, List(), c)}
//  var source: PackratParser[SourceCode] = rep(ws(function | _class)) ^^ (a => SourceCode(filterByType[Class](a), filterByType[Function](a), List(), List()))
}

object NightParser extends SimpleParsers {

  case class ParseError(message: String, line: Int, column: Int)

  private val classParsers = Map[java.lang.Class[_], () => P[_]](
//    (classOf[LiteralInt], int),
//    (classOf[LiteralString], string),
//    (classOf[Type], _type),
//    (classOf[Parameter], parameter),
//    (classOf[Expression], expression),
//    (classOf[If], expression),
//    (classOf[Function], function),
//    (classOf[Class], _class),
//    (classOf[SourceCode], source)
  )
//
  def parse[T](code: CharSequence)(implicit tag: ClassTag[T]):Either[ParseError, T] = {
      tag match {
        case LiteralBoolean.getClass => parseBool(code)
      }
//    if (!classParsers.contains(tag.runtimeClass)){
//      throw new RuntimeException("Invalid class: "+tag.runtimeClass.getName)
//    }
//    val parser: NightParser.Parser[_] = classParsers(tag.runtimeClass)
//    parseAll(parser, code).asInstanceOf[ParseResult[T]] match {
//      case Success(matched, _) => Right(matched)
//      case NoSuccess(result, next) => Left(ParseError(result, next.pos.line, next.pos.column))
//    }
  }

  def main(args: Array[String]): Unit = {
//    parse[Function](
//      """
//        |fun test(a: A):B { 1 + 2}
//      """.stripMargin.trim) match {
//      case Right(matched) => println(matched)
//      case Left(error) => println("FAILURE: " + error.message +" at "+error.line+":"+error.column)
//    }
  }
}
