package pl.msitko.refined.runtime

import pl.msitko.refined.Refined
import pl.msitko.refined.auto._

class ValidateStringSpec extends munit.FunSuite {
  test("should work for StartsWith") {
    type Pred = StartsWith["abc"]
    val correctValues = List("abc", "abcd", "abcde")
    assert(clue(correctValues.map(Refined.refineV[Pred](_))).forall(_.isRight))
  }
  test("should work for StartsWith - negative cases") {
    type Pred = StartsWith["abc"]
    assertEquals(Refined.refineV[Pred]("abd"), Left("Validation of refined type failed: abd.startsWith(abc)"))
  }
  test("should work for Or") {
    type Pred = Or[StartsWith["abc"], EndsWith["xyz"]]
    val correctValues = List("abc", "abcd", "xyz", "fxyz")
    assert(clue(correctValues.map(Refined.refineV[Pred](_))).forall(_.isRight))
  }
}
