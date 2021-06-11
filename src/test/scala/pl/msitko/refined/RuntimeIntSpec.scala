package pl.msitko.refined

import pl.msitko.refined.auto._
import pl.msitko.refined.Refined

class RuntimeIntSpec extends munit.FunSuite {
  test("should work for GreaterThan") {
    assertEquals(Refined.refineV[GreaterThan[5]](6), Right(Refined.unsafeApply[Int, GreaterThan[5]](6)))
  }

  test("should work for GreaterThan - negative cases") {
    assertEquals(Refined.refineV[GreaterThan[5]](5), Left("Validation of refined type failed: 5 > 5"))
    assertEquals(Refined.refineV[GreaterThan[5]](4), Left("Validation of refined type failed: 4 > 5"))
  }
  
}
