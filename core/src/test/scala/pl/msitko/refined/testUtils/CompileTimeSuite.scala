package pl.msitko.refined.testUtils

import scala.compiletime.testing.typeCheckErrors
import scala.compiletime.testing.Error

trait CompileTimeSuite extends munit.FunSuite {

  def shouldContain(errors: List[Error], expectedError: String) =
    assert(clue(errors.map(_.message)).contains(clue(expectedError)))

}
