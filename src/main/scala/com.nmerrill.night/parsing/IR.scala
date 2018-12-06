package com.nmerrill.night.parsing

import com.nmerrill.night.parsing.BinaryOperator.BinaryOperator
import com.nmerrill.night.parsing.DeclarationModifier.DeclarationModifier
import com.nmerrill.night.parsing.FlowStatementType.FlowStatementType
import com.nmerrill.night.parsing.UnaryOperator.UnaryOperator
import com.sun.org.apache.xpath.internal.operations.{And, Or}


trait Statement {

}

trait Expression extends Statement{

}

trait Literal extends Expression {

}

object Unit extends Expression

case class LiteralInt(int: String) extends Literal
case class LiteralString(string: String) extends Literal
case class LiteralBoolean(bool: Boolean) extends Literal

case class Type(_type: String) // TODO
case class Parameter(name: String, _type: Type, modifiers: List[DeclarationModifier])
case class Clause(expr: Expression) // TODO

case class BinaryOperation(expr1: Expression, operator: BinaryOperator, expr2: Expression) extends Expression
case class UnaryOperation(expr1: Expression, operator: UnaryOperator) extends Expression // TODO
case class PropertyAccess(expr: Expression, name: String) extends Expression
case class FunctionCall(expr: Expression, arguments: List[Expression]) extends Expression // TODO: named arguments
case class VariableDeclaration(name: String, _type: Option[Type], value: Expression, modifiers: List[DeclarationModifier]) extends Statement // TODO
case class FlowStatement(_type: FlowStatementType, _if: Option[Expression]) extends Statement // TODO

case class If(condition: Expression, truthy: List[Statement], falsy: List[Statement]) extends Expression
case class Loop(condition: Option[Expression], body: List[Statement], as: Option[Parameter]) extends Statement //TODO
case class Function(name: String, parameters: List[Parameter], returnType: Type, clauses: List[Clause], body: List[Statement]) extends Statement //TODO: clauses
case class Class(name: String, parameters: List[Parameter], properties: List[VariableDeclaration], functions: List[Function]) //TODO: properties
case class Import(path: String) //TODO
case class SourceCode(classes: List[Class], functions: List[Function], imports: List[Import], constants: List[VariableDeclaration]) //TODO: imports, constants

object BinaryOperator extends Enumeration {
  type BinaryOperator = Value
  var Addition, Subtraction, Multiplication, IntegerDivision, Modulo,
  Equals, NotEquals,
  GreaterThan, LessThan, GreaterThanOrEquals, LessThanOrEquals,
  And, Or
  = Value
}

object UnaryOperator extends Enumeration {
  type UnaryOperator = Value
  var Negation, Not = Value
}

object DeclarationModifier extends Enumeration {
  type DeclarationModifier = Value
  var Val, Var, Mut, Immut = Value
}

object FlowStatementType extends Enumeration {
  type FlowStatementType = Value
  var Continue, Break, Yield, Return = Value
}

