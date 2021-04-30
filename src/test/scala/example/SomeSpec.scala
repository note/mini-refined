package example

import pl.msitko.refined.Refined
import pl.msitko.refined.ValidateExpr.GreaterThan
import munit.Assertions.assert
import scala.compiletime.testing.typeCheckErrors

class SomeSpec extends munit.FunSuite {
  test("2 + 2 should equal 4") {
    assertEquals(2 + 2, 4)
  }
  test("Refined.unsafeApply should not compile outside of pl.msitko.refined package") {
    val errors = typeCheckErrors("Refined.unsafeApply[34, GreaterThan[10]](34)")
    assert(clue(errors).nonEmpty)
    assert(clue(errors.head.message).contains("method unsafeApply cannot be accessed as a member"))
  }
}
