package pl.msitko.refined

import pl.msitko.refined.Refined
import pl.msitko.refined.Refined._
import pl.msitko.refined.ValidateExpr._
import pl.msitko.refined.auto._
import pl.msitko.refined.testUtils.CompileTimeSuite

import scala.compiletime.testing.{typeCheckErrors => errors}

class ListSpec extends CompileTimeSuite {
  test("Size[GreaterThan] should pass") {
    val a: List[String] Refined Size[GreaterThan[1]] = List("a", "b")
    val b: List[String] Refined Size[GreaterThan[1]] = List("a", "b", "c")
  }
  test("Size[GreaterThan] should fail for incorrect value") {
    shouldContain(errors("val a: List[String] Refined Size[GreaterThan[1]] = List.empty"),
            "Validation failed: list size doesn't hold predicate: 0 > 1")
    shouldContain(errors("val a: List[String] Refined Size[GreaterThan[1]] = List(\"a\")"),
            "Validation failed: list size doesn't hold predicate: 1 > 1")
  }

}
