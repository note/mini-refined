package example

import pl.msitko.refined.Refined
import pl.msitko.refined.Refined._
import pl.msitko.refined.ValidateExpr.StartsWith
import pl.msitko.refined.auto.mkValidatedString
import pl.msitko.refined.testUtils.CompileTimeSuite

import scala.compiletime.testing.{typeCheckErrors => errors}

class StringSpec extends CompileTimeSuite {
  test("""StartsWith["abc] should fail for incorrect input""") {
    failCompilationWith(errors("""mkValidatedString["abd", StartsWith["abc"]]("abd")"""),
      "Validation failed: abd.startsWith(abc)")
  }
  test("""StartsWith["abc] should pass""") {
    val a: String Refined StartsWith["abc"] = mkValidatedString["abcd", StartsWith["abc"]]("abcd")
    assertEquals(a + "", "abcd")
    val a2: String Refined StartsWith["abc"] = mkValidatedString["abc", StartsWith["abc"]]("abc")
    assertEquals(a2 + "", "abc")
  }
}
