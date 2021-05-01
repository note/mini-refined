package example

import pl.msitko.refined.{Refined, RefinedLift}
import pl.msitko.refined.ValidateExpr._
import munit.Assertions.assert
import pl.msitko.refined.testUtils.CompileTimeSuite

import scala.compiletime.testing.{typeCheckErrors => errors}

class BasicSpec extends CompileTimeSuite {
  test("GreaterThan[10] should fail for lower or equal to to") {
    failCompilationWith(errors("RefinedLift.mkValidatedInt[7, GreaterThan[10]](7)"),
                  "Validation failed: 7 < 10")
    failCompilationWith(errors("RefinedLift.mkValidatedInt[10, GreaterThan[10]](10)"),
                  "Validation failed: 10 < 10")
  }
  test("GreaterThan[10] should pass for greater than 10") {
    val a: Int Refined GreaterThan[10] = RefinedLift.mkValidatedInt[16, GreaterThan[10]](16)
    assertEquals[Any, Any](a, 16)
  }
  test("Refined.unsafeApply should not compile outside of pl.msitko.refined package") {
    val es = errors("Refined.unsafeApply[34, GreaterThan[10]](34)")
    assert(clue(es.head.message).contains("method unsafeApply cannot be accessed as a member"))
  }
}
