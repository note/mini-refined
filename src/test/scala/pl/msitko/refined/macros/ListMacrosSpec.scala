package pl.msitko.refined.macros

import pl.msitko.refined.ValidateList
import pl.msitko.refined.testUtils.CompileTimeSuite
import scala.compiletime.testing.{typeCheckErrors => errors}

final case class TestData(a: Int, b: String)

class ListMacrosSpec extends CompileTimeSuite {
  test("should work for non-empty lists") {
    assertEquals(ListMacros.listSize(List(1,2,3)), 3)
    assertEquals(ListMacros.listSize(List("a", "b")), 2)
    assertEquals(ListMacros.listSize(List(TestData(0, "ab"), TestData(1, "ab"), TestData(2, "ab"), TestData(3, "ab"))), 4)
  }
  test("should work for empty lists") {
    assertEquals(ListMacros.listSize(List.empty[Int]), 0)
    assertEquals(ListMacros.listSize(Nil), 0)
    assertEquals(ListMacros.listSize(scala.Nil), 0)
  }
  test("should fail compilation in case list size cannot be determined at compile time") {
    val e1 = errors("ListMacros.listSize(List.fill(10)(\"abc\"))")
    assert(clue(e1.head.message).startsWith("Cannot determine size of list in compiletime"))
  }
  test("should fail compilation in case list size cannot be determined at compile time (2)") {
    val xs = List("a", "b")
    val e1 = errors("ListMacros.listSize(xs)")
    assert(clue(e1.head.message).startsWith("Cannot determine size of list in compiletime"))
  }
}
