package pl.msitko.refined

import pl.msitko.refined.Refined
import pl.msitko.refined.Refined._
import pl.msitko.refined.auto._
import pl.msitko.refined.compiletime.ValidateExprString.EndsWith
import pl.msitko.refined.testUtils.CompileTimeSuite

import scala.compiletime.testing.{typeCheckErrors => errors}

class StringSpec extends CompileTimeSuite {
  test("""StartsWith["abc] should fail for incorrect input""") {
    shouldContain(
      errors("""mkValidatedString["abd", StartsWith["abc"]]("abd")"""),
      "Validation failed: abd.startsWith(abc)")
  }
  test("""StartsWith["abc] should pass""") {
    val a: String Refined StartsWith["abc"] = mkValidatedString["abcd", StartsWith["abc"]]("abcd")
    assertEquals(a + "", "abcd")
    val a2: String Refined StartsWith["abc"] = mkValidatedString["abc", StartsWith["abc"]]("abc")
    assertEquals(a2 + "", "abc")
  }
  test("should work with Or") {
    val a: String Refined Or[StartsWith["abc"], EndsWith["xyz"]] = "abcd"
    assertEquals(a + "", "abcd")
    val a2: String Refined Or[StartsWith["abc"], EndsWith["xyz"]] = "axyz"
    assertEquals(a2 + "", "axyz")
  }
  test("should work with Or - negative cases") {
    shouldContain(
      errors("""val a: String Refined Or[StartsWith["abc"], EndsWith["xyz"]] = "abyz""""),
      "Validation failed: (abyz.startsWith(abc) Or abyz.endsWith(xyz)), predicate failed: abyz.endsWith(xyz)")
  }
  test("should work with And") {
    val a: String Refined And[StartsWith["abc"], EndsWith["xyz"]] = "abcdxyz"
    val a2: String Refined Or[StartsWith["abc"], EndsWith["xyz"]] = "axcxyz"
  }
  test("should work with And - negative cases") {
    shouldContain(
      errors("""val a: String Refined And[StartsWith["abc"], EndsWith["xyz"]] = "abyz""""),
      "Validation failed: (abyz.startsWith(abc) And abyz.endsWith(xyz)), predicate failed: abyz.startsWith(abc)")
  }
}
