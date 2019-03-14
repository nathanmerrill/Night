package com.nmerrill.night.parsing
import fastparse.{Parsed, _}
import NoWhitespace._


object SimpleParsers {

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

//  def identifier: Parser[String] = ws("""[_a-zA-Z][_a-zA-Z0-9]*""".r)
//
//  def _type: Parser[Type] = identifier ^^ Type
//
//  def parameter: Parser[Parameter] = identifier ~ (ws(":") ~> _type) ^^ { case a ~ b => Parameter(a, b, List()) }
//
//  def parameterList: Parser[List[Parameter]] = repsep(parameter, ws(","))
//
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

object NightParser {

  private def bool[_: P]: P[LiteralBoolean] = P(("false" | "true").!).map(_.eq("true")).map(LiteralBoolean)
  private def integer[_: P]: P[LiteralInt] = P( ("-".? ~ CharIn("0-9").rep(1)).! ~ &(CharPred(c => !c.isLetter && c != '.') | End)).map(LiteralInt).opaque("integer")
  private def singleQuoteString[_: P]: P[LiteralString] = P( "\'" ~/ CharsWhile(c => c != '\'').! ~ "\'").map(LiteralString)
  private def doubleQuoteString[_: P]: P[LiteralString] = P( "\"" ~/ CharsWhile(c => c != '\"').! ~ "\"").map(LiteralString)
  private def string[_: P]: P[LiteralString] = P(singleQuoteString | doubleQuoteString)
  private def literal[_: P]: P[Literal] = P(string | bool | integer)

  private def parens[_: P]: P[Expression] = P( "(" ~/ and ~ ")" )
  private def factor[_: P]: P[Expression] = P( literal | parens )
  private def divMul[_: P]: P[Expression] = P( factor ~ (StringIn("*","div","mod").! ~/ factor).rep ).map(combineBinaryOperators)
  private def addSub[_: P]: P[Expression] = P( divMul ~ (CharIn("+\\-").! ~/ divMul).rep ).map(combineBinaryOperators)
  private def compare[_: P]: P[Expression] = P( addSub ~ (StringIn("lt","gt","eq","neq","lteq","gteq").! ~/ addSub).rep).map(combineComparisons)
  private def or[_: P]: P[Expression] = P( compare ~ ("or".! ~/ compare).rep).map(combineBinaryOperators)
  private def and[_: P]: P[Expression] = P( or ~ ("and".! ~/ or).rep).map(combineBinaryOperators)
  private def expr[_: P]: P[Expression]   = P( and ~ End )

  private def foldExpression(f: (Expression, String, Expression) => Expression)(tup: (Expression, Seq[(String, Expression)])): Expression = {
    val (operand, operations) = tup
    operations.foldLeft(operand)((left, tup2) => {
      val (operator, right) = tup2
      f(left, operator, right)
    })
  }

  private def combineBinaryOperators: ((Expression, Seq[(String, Expression)])) => Expression =
    foldExpression( (left, op, right) =>
      BinaryOperation(left, op match {
        case "*" => BinaryOperator.Multiplication
        case "+" => BinaryOperator.Addition
        case "-" => BinaryOperator.Subtraction
        case "div" => BinaryOperator.IntegerDivision
        case "mod" => BinaryOperator.Modulo
        case "and" => BinaryOperator.And
        case "or" => BinaryOperator.Or
      }, right)
    )

  private def combineComparisons(tup: (Expression, Seq[(String, Expression)])): Expression = {
    val (operand, operations) = tup
    val leftOperands = operand +: operations.map(_._2)
    val comparisons = leftOperands.zip(operations).map(tup => {
      val (left, tup2) = tup
      val (op, right) = tup2
      BinaryOperation(left, op match {
        case "lt" => BinaryOperator.LessThan
        case "gt" => BinaryOperator.GreaterThan
        case "eq" => BinaryOperator.Equals
        case "neq" => BinaryOperator.NotEquals
        case "lteq" => BinaryOperator.LessThanOrEquals
        case "gteq" => BinaryOperator.GreaterThanOrEquals
      }, right)
    })
    comparisons.reduceLeft((left, right) => {
      BinaryOperation(left, BinaryOperator.And, right)
    })
  }

  def parseBool(code: String): Parsed[LiteralBoolean] = {
    fastparse.parse(code, parser => {
      bool(parser)
    })
  }

  def parseInteger(code: String): Parsed[LiteralInt] = {
    fastparse.parse(code, integer(_))
  }

  def parseString(code: String): Parsed[LiteralString] = {
    fastparse.parse(code, string(_))
  }

  def parseLiteral(code: String): Parsed[Literal] = {
    fastparse.parse(code, literal(_))
  }

  def parseExpression(code: String): Parsed[Expression] = {
    fastparse.parse(code, expr(_))
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
