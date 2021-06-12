package pl.msitko.refined.runtime

import pl.msitko.refined.Refined
import pl.msitko.refined.auto._
import pl.msitko.refined.compiletime.ValidateExprString.{EndsWith, StartsWith}

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
  test("should work for Or - negative cases") {
    type Pred = Or[StartsWith["abc"], EndsWith["xyz"]]
    val incorrectValues = List("abd", "abyz")
    assert(clue(incorrectValues.map(Refined.refineV[Pred](_))).forall(_.isLeft))
  }
  test("should work for nested Or(And, And)") {
    type Pred = Or[And[StartsWith["abc"], EndsWith["xyz"]], And[StartsWith["123"], EndsWith["abc"]]]
    val correctValues = List("abcxyz", "abcdefxyz", "123abc", "123ppppabc")
    val incorrectValues = List("abxyz", "abcyz", "pppooo", "123bc", "12abc", "12aaabc")

    assert(clue(correctValues.map(Refined.refineV[Pred](_))).forall(_.isRight))
    assert(clue(incorrectValues.map(Refined.refineV[Pred](_))).forall(_.isLeft))
  }
}
