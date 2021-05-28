package pl.msitko.refined

import munit.Assertions.assert
import pl.msitko.refined.Refined._
import pl.msitko.refined.ValidateExpr._
import pl.msitko.refined.auto._
import pl.msitko.refined.testUtils.CompileTimeSuite
import pl.msitko.refined.{Refined, ValidateList}

import scala.compiletime.testing.{typeCheckErrors => errors}
import scala.language.implicitConversions

class IntSpec extends CompileTimeSuite {
  test("GreaterThan[10] should fail for lower or equal to to") {
    shouldContain(errors("mkValidatedInt[7, GreaterThan[10]](7)"), "Validation failed: 7 > 10")
    shouldContain(errors("mkValidatedInt[10, GreaterThan[10]](10)"), "Validation failed: 10 > 10")
  }
  test("GreaterThan[10] should fail for lower or equal to to (implicitly)") {
    shouldContain(errors("val a: Int Refined GreaterThan[10] = 7"), "Validation failed: 7 > 10")
  }
  test("GreaterThan[10] should pass for greater than 10") {
    val a: Int Refined GreaterThan[10] = mkValidatedInt[16, GreaterThan[10]](16)
    val xy: Int                        = 45
    val xyz                            = xy + 32
    assertEquals(a + 0, 16)
  }
  test("GreaterThan[10] should pass for greater than 10 (implicitly)") {
    val a: Refined[Int, GreaterThan[10]] = 16
    assertEquals(a + 0, 16)
  }
  test("GreaterThan And LowerThan") {
    val a: Int Refined And[GreaterThan[10], LowerThan[20]] = 15
  }
  test("GreaterThan And LowerThan - failure") {
    shouldContain(
      errors("val a: Int Refined And[GreaterThan[10], LowerThan[20]] = 5"),
      "Validation failed: (5 > 10 And 5 < 20), predicate failed: 5 > 10")
    shouldContain(
      errors("val b: Int Refined And[GreaterThan[10], LowerThan[20]] = 25"),
      "Validation failed: (25 > 10 And 25 < 20), predicate failed: 25 < 20")
  }
  test("nested boolean conditions") {
    val a: Int Refined Or[And[GreaterThan[10], LowerThan[20]], And[GreaterThan[110], LowerThan[120]]] = 15
    val b: Int Refined Or[And[GreaterThan[10], LowerThan[20]], And[GreaterThan[110], LowerThan[120]]] = 115
  }
  test("nested boolean conditions - failure") {
    shouldContain(
      errors("val a: Int Refined Or[And[GreaterThan[10], LowerThan[20]], And[GreaterThan[110], LowerThan[120]]] = 5"),
      "Validation failed: ((5 > 10 And 5 < 20) Or (5 > 110 And 5 < 120))")
    shouldContain(
      errors("val a: Int Refined Or[And[GreaterThan[10], LowerThan[20]], And[GreaterThan[110], LowerThan[120]]] = 35"),
      "Validation failed: ((35 > 10 And 35 < 20) Or (35 > 110 And 35 < 120))")
    shouldContain(
      errors("val a: Int Refined Or[And[GreaterThan[10], LowerThan[20]], And[GreaterThan[110], LowerThan[120]]] = 125"),
      "Validation failed: ((125 > 10 And 125 < 20) Or (125 > 110 And 125 < 120))")
  }
  test("basic inference (GreaterThan)") {
    val a: Int Refined GreaterThan[10] = 16
    val b: Int Refined GreaterThan[5]  = a
    shouldContain(errors("val c: Int Refined GreaterThan[15] = a"), "Cannot be inferred")

  }
  test("basic inference (LowerThan)") {
    val a: Int Refined LowerThan[10] = 7
    val b: Int Refined LowerThan[15] = a
    shouldContain(errors("val c: Int Refined LowerThan[5] = a"), "Cannot be inferred")
  }
}
