package com.nmerrill.night.parsing

import fastparse.Parsed
import org.scalatest.{FlatSpec, Matchers}

import scala.reflect.ClassTag


class ParserSpec extends FlatSpec with Matchers {

  def parseSuccess[T](code: String, expected: T)(implicit tag: ClassTag[T]){
    val result = NightParser.parse[T](code)
    result.isSuccess shouldBe true
    result.get shouldBe expected
  }

  def parseFailure[T](code: String, message: String)(implicit tag: ClassTag[T]){
    val result = NightParser.parse[T](code)
    result.isSuccess shouldBe false
    result shouldBe a [Parsed.Failure]
    val Parsed.Failure(failureString, _, _) = result
    failureString should include (message)
  }

  "The parser" should "handle integers" in {
    parseSuccess("1", LiteralInt("1"))
    parseSuccess("0", LiteralInt("0"))
    parseSuccess("-1", LiteralInt("-1"))
    parseSuccess("1234567890", LiteralInt("1234567890"))
    parseSuccess("100000000000000000", LiteralInt("100000000000000000"))
    parseSuccess("-100000000000000000", LiteralInt("-100000000000000000"))
    parseFailure[LiteralInt]("a1", "\"a1\" is an invalid integer")
    parseFailure[LiteralInt]("#1", "\"#1\" is an invalid integer")
    parseFailure[LiteralInt]("1a", "\"1a\" is an invalid integer")
    parseFailure[LiteralInt]("10.1", "\"10.1\" is an invalid integer")
    parseFailure[LiteralInt]("-10.1", "\"-10.1\" is an invalid integer")
  }

  "The parser" should "handle booleans" in {
    parseSuccess("false", LiteralBoolean(false))
    parseSuccess("true", LiteralBoolean(true))
    parseFailure[LiteralBoolean]("False", "\"False\" is not a valid boolean value")
    parseFailure[LiteralBoolean]("True", "\"True\" is not a valid boolean value")
    parseFailure[LiteralBoolean]("FALSE", "\"FALSE\" is not a valid boolean value")
    parseFailure[LiteralBoolean]("TRUE", "\"TRUE\" is not a valid boolean value")
    parseFailure[LiteralBoolean]("f", "\"f\" is not a valid boolean value")
    parseFailure[LiteralBoolean]("t", "\"t\" is not a valid boolean value")
  }
}
