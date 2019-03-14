package com.nmerrill.night.parsing

import fastparse.Parsed
import org.scalatest._
import prop._


class ParserSpec extends FreeSpec with TableDrivenPropertyChecks with Matchers {

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
          result.asInstanceOf[Parsed.Failure].msg shouldBe "Expected integer:1:1, found \"" + n + "\""
        }
      }
    }
  }
}
