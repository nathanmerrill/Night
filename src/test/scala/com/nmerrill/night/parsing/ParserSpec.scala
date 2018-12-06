package com.nmerrill.night.parsing

import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.prop.TableDrivenPropertyChecks._


class ParserSpec extends FlatSpec with Matchers {

//  def parseSuccess[T](code: String, expected: T)(implicit tag: ClassTag[T]){
//    val result = NightParser.parse[T](code)
//    result.isSuccess shouldBe true
//    result.get shouldBe expected
//  }
//
//  def parseFailure[T](code: String, message: String)(implicit tag: ClassTag[T]){
//    val result = NightParser.parse[T](code)
//    result.isSuccess shouldBe false
//    result shouldBe a [Parsed.Failure]
//    val Parsed.Failure(failureString, _, _) = result
//    failureString should include (message)
//  }

    val invalidIntegers = Table("n", "a1", "#1", "1a", "10.1", "-10.1")

  val validIntegers = Table("n", "0", "1", "-1", "1234567890", "100000000000000000", "-100000000000000000")

  "The parser" should "parse integers" in {
    forAll(validIntegers) { (n: String) =>
      val result = NightParser.parseInteger(n)
      result.isSuccess shouldBe true
      result.get.value shouldBe LiteralInt(n)
    }
  }

//    forAll(invalidIntegers) { n: String =>
////      "The parser" should " fail to parse " + n + " as an integer" in {
//        val result = NightParser.parseInteger(n)
//        result.isSuccess shouldBe false
//        result.asInstanceOf[Parsed.Failure].label shouldBe "\"" + n + "\" is an invalid integer"
////      }
//    }
//  }



//  "The parser" should "handle integers" in {
//    val valid =
//    valid.foreach{n =>
//      val result = NightParser.parseInteger(n)
//      result.isSuccess shouldBe true
//      result.get.value shouldBe LiteralInt(n)
//    }
//    val invalid = List("a1", "#1", "1a", "10.1", "-10.1")
//    invalid.foreach{n =>
//      val result = NightParser.parseInteger(n)
//      result.isSuccess shouldBe false
//      result.asInstanceOf[Parsed.Failure].label shouldBe "\""+n+"\" is an invalid integer"
//    }
//  }

  "The parser" should "handle booleans" in {
//    parseSuccess(NightParser.parseBool("false"), LiteralBoolean(false))
//    parseSuccess("true", LiteralBoolean(true))
//    parseFailure[LiteralBoolean]("False", "\"False\" is not a valid boolean value")
//    parseFailure[LiteralBoolean]("True", "\"True\" is not a valid boolean value")
//    parseFailure[LiteralBoolean]("FALSE", "\"FALSE\" is not a valid boolean value")
//    parseFailure[LiteralBoolean]("TRUE", "\"TRUE\" is not a valid boolean value")
//    parseFailure[LiteralBoolean]("f", "\"f\" is not a valid boolean value")
//    parseFailure[LiteralBoolean]("t", "\"t\" is not a valid boolean value")
  }
}
