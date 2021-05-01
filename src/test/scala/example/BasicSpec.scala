package example

import pl.msitko.refined.{Refined, auto}
import pl.msitko.refined.ValidateExpr._
import munit.Assertions.assert
import pl.msitko.refined.testUtils.CompileTimeSuite

import scala.compiletime.testing.{typeCheckErrors => errors}

class BasicSpec extends CompileTimeSuite {
  test("GreaterThan[10] should fail for lower or equal to to") {
    failCompilationWith(errors("auto.mkValidatedInt[7, GreaterThan[10]](7)"),
                  "Validation failed: 7 < 10")
    failCompilationWith(errors("auto.mkValidatedInt[10, GreaterThan[10]](10)"),
                  "Validation failed: 10 < 10")
  }
  test("GreaterThan[10] should pass for greater than 10") {
    val a: Int Refined GreaterThan[10] = auto.mkValidatedInt[16, GreaterThan[10]](16)
    assertEquals[Any, Any](a, 16)
  }
  test("""StartsWith["abc] should fail for incorrect input""") {
    failCompilationWith(errors("""auto.mkValidatedString["abd", StartsWith["abc"]]("abd")"""),
                  "Validation failed: abd.startsWith(abc)")
  }
  test("""StartsWith["abc] should pass""") {
    val a: String Refined StartsWith["abc"] = auto.mkValidatedString["abcd", StartsWith["abc"]]("abcd")
    assertEquals[Any, Any](a, "abcd")
    val a2: String Refined StartsWith["abc"] = auto.mkValidatedString["abc", StartsWith["abc"]]("abc")
    assertEquals[Any, Any](a2, "abc")
  }
  test("Refined.unsafeApply should not compile outside of pl.msitko.refined package") {
    val es = errors("Refined.unsafeApply[34, GreaterThan[10]](34)")
    assert(clue(es.head.message).contains("none of the overloaded alternatives named unsafeApply can be accessed as a member"))
  }
}
