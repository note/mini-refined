package example

import munit.Assertions.assert
import scala.compiletime.testing.{typeCheckErrors => errors}
import pl.msitko.refined.testUtils.CompileTimeSuite
import pl.msitko.refined.Refined

// This test is out side of pl.msitko.refined so we can test some methods are not accessible
class RefinedAccessSpec extends CompileTimeSuite {
  test("Refined.unsafeApply should not compile outside of pl.msitko.refined package") {
    val es = errors("Refined.unsafeApply[34, GreaterThan[10]](34)")
    assert(clue(es.head.message).contains("none of the overloaded alternatives named unsafeApply can be accessed as a member"))
  }
}
