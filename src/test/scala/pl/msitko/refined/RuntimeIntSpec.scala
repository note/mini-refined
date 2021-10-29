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

  test("should work for Or") {
    type Pred = Or[GreaterThan[100], LowerThan[10]]
    val res = Refined.refineV[Pred](9)
    assertEquals(res, Right(Refined.unsafeApply[Int, Pred](9)))
    val res2 = Refined.refineV[Pred](101)
    assertEquals(res2, Right(Refined.unsafeApply[Int, Pred](101)))
  }

  test("should work for Or - negative cases") {
    type Pred = Or[GreaterThan[100], LowerThan[10]]
    val res = Refined.refineV[Pred](10)
    assertEquals(res, Left("Validation of refined type failed: (10 > 100 Or 10 < 10)"))
    val res2 = Refined.refineV[Pred](100)
    assertEquals(res2, Left("Validation of refined type failed: (100 > 100 Or 100 < 10)"))
  }

}
