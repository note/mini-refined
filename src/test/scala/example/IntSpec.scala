package example

import pl.msitko.refined.Refined
import pl.msitko.refined.Refined._
import pl.msitko.refined.auto._
import pl.msitko.refined.ValidateExpr._
import munit.Assertions.assert
import pl.msitko.refined.testUtils.CompileTimeSuite
import scala.language.implicitConversions

import scala.compiletime.testing.{typeCheckErrors => errors}

class IntSpec extends CompileTimeSuite {
  test("GreaterThan[10] should fail for lower or equal to to") {
    failCompilationWith(errors("mkValidatedInt[7, GreaterThan[10]](7)"),
                  "Validation failed: 7 > 10")
    failCompilationWith(errors("mkValidatedInt[10, GreaterThan[10]](10)"),
                  "Validation failed: 10 > 10")
  }
  test("GreaterThan[10] should fail for lower or equal to to (implicitly)") {
    failCompilationWith(errors("val a: Int Refined GreaterThan[10] = 7"),
      "Validation failed: 7 > 10")
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
  test("GreaterThan And LowerThan") {
    val a: Int Refined And[GreaterThan[10], LowerThan[20]] = 15
  }
  test("GreaterThan And LowerThan - fail") {
    failCompilationWith(errors("val a: Int Refined And[GreaterThan[10], LowerThan[20]] = 5"),
                  "Validation failed: 5 > 10")
    failCompilationWith(errors("val a: Int Refined And[GreaterThan[10], LowerThan[20]] = 25"),
      "Validation failed: 25 < 20")
  }
  test("inference") {
    // TODO:
    val a: Refined[Int, GreaterThan[10]] = 16
//    val b: Refined[Int, GreaterThan[5]] = a
  }
  test("Refined.unsafeApply should not compile outside of pl.msitko.refined package") {
    val es = errors("Refined.unsafeApply[34, GreaterThan[10]](34)")
    assert(clue(es.head.message).contains("none of the overloaded alternatives named unsafeApply can be accessed as a member"))
  }
}
