package example

import pl.msitko.refined.{Refined}
import pl.msitko.refined.Refined._
import pl.msitko.refined.auto._
import pl.msitko.refined.ValidateExpr._
import munit.Assertions.assert
import pl.msitko.refined.testUtils.CompileTimeSuite
import scala.language.implicitConversions

import scala.compiletime.testing.{typeCheckErrors => errors}

class BasicSpec extends CompileTimeSuite {
  test("GreaterThan[10] should fail for lower or equal to to") {
    failCompilationWith(errors("mkValidatedInt[7, GreaterThan[10]](7)"),
                  "Validation failed: 7 < 10")
    failCompilationWith(errors("mkValidatedInt[10, GreaterThan[10]](10)"),
                  "Validation failed: 10 < 10")
  }
  test("GreaterThan[10] should fail for lower or equal to to (implicitly)") {
    failCompilationWith(errors("val a: Int Refined GreaterThan[10] = 7"),
      "Validation failed: 7 < 10")
  }
  test("GreaterThan[10] should pass for greater than 10") {
    val a: Int Refined GreaterThan[10] = mkValidatedInt[16, GreaterThan[10]](16)
    val xy: Int = 45
    val xyz = xy + 32
    assertEquals(a + 0, 16)
  }
  test("GreaterThan[10] should pass for greater than 10 (implicitly)") {
    val a: Refined[Int, GreaterThan[10]] = 16
    assertEquals(a + 0, 16)
  }
  test("""StartsWith["abc] should fail for incorrect input""") {
    failCompilationWith(errors("""mkValidatedString["abd", StartsWith["abc"]]("abd")"""),
                  "Validation failed: abd.startsWith(abc)")
  }
  test("""StartsWith["abc] should pass""") {
    val a: String Refined StartsWith["abc"] = mkValidatedString["abcd", StartsWith["abc"]]("abcd")
    assertEquals(a + "", "abcd")
    val a2: String Refined StartsWith["abc"] = mkValidatedString["abc", StartsWith["abc"]]("abc")
    assertEquals(a2 + "", "abc")
  }
  test("Refined.unsafeApply should not compile outside of pl.msitko.refined package") {
    val es = errors("Refined.unsafeApply[34, GreaterThan[10]](34)")
    assert(clue(es.head.message).contains("none of the overloaded alternatives named unsafeApply can be accessed as a member"))
  }
}
