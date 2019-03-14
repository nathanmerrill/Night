package com.nmerrill.night.parsing

import fastparse.Parsed
import org.scalatest._
import prop._


class ParserSpec extends FreeSpec with TableDrivenPropertyChecks with Matchers {


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
  "NightParser" - {
    "integers" - {
      val validIntegers = Table("n", "0", "1", "-1", "1234567890", "100000000000000000", "-100000000000000000")
      forAll(validIntegers) { n =>
        s"should parse $n" in {
          val result = NightParser.parseInteger(n)
          result.isSuccess shouldBe true
          result.get.value shouldBe LiteralInt(n)
        }
      }

      val invalidIntegers = Table("n", "a1", "#1", "1a", "10.1", "-10.1")
      forAll(invalidIntegers) { n: String =>
        s"should fail to parse $n" in {
          val result = NightParser.parseInteger(n)
          result.isSuccess shouldBe false
          result.asInstanceOf[Parsed.Failure].msg shouldBe "Expected integer:1:1, found \""+n+"\""
        }
      }
    }
  }



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

//  "The parser" should "handle booleans" in {
//    parseSuccess(NightParser.parseBool("false"), LiteralBoolean(false))
//    parseSuccess("true", LiteralBoolean(true))
//    parseFailure[LiteralBoolean]("False", "\"False\" is not a valid boolean value")
//    parseFailure[LiteralBoolean]("True", "\"True\" is not a valid boolean value")
//    parseFailure[LiteralBoolean]("FALSE", "\"FALSE\" is not a valid boolean value")
//    parseFailure[LiteralBoolean]("TRUE", "\"TRUE\" is not a valid boolean value")
//    parseFailure[LiteralBoolean]("f", "\"f\" is not a valid boolean value")
//    parseFailure[LiteralBoolean]("t", "\"t\" is not a valid boolean value")
//  }
}
