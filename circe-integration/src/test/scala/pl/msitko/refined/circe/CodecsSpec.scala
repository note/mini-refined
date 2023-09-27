package pl.msitko.refined.circe

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.syntax.*
import io.circe.{parser, Decoder, Encoder, Printer}
import pl.msitko.refined.auto.*
import pl.msitko.refined.compiletime.ValidateExprInt
import pl.msitko.refined.compiletime.ValidateExprInt.GreaterThan
import pl.msitko.refined.Refined

final case class Library(
    name: String Refined StartsWith["lib"],
    version: Int Refined GreaterThan[10],
    dependencies: List[String] Refined Size[GreaterThan[1]])

class CodecsSpec extends munit.FunSuite:
  given enc: Encoder[Library] = deriveEncoder[Library]
  given dec: Decoder[Library] = deriveDecoder[Library]

  test("should roundtrip for a manually defined Encoder and Decoder for type Library") {
    val in             = Library("libA", 23, List("depA", "depB"))
    val encoded        = in.asJson.printWith(Printer.spaces2)
    val Right(decoded) = parser.parse(encoded).flatMap(_.as[Library]): @unchecked

    assertEquals(decoded, in)
  }

  test("decoder should fail for incorrect Library.name") {
    val in = """{
               |  "name" : "something",
               |  "version" : 11,
               |  "dependencies": ["depA", "depB"]
               |}""".stripMargin

    val Left(decodingError) = parser.parse(in).flatMap(_.as[Library]): @unchecked
    assertEquals(
      decodingError.getMessage,
      "DecodingFailure at .name: Validation of refined type failed: something.startWith(lib)")
  }

  test("decoder should fail for incorrect Library.version") {
    val in = """{
               |  "name" : "libA",
               |  "version" : 7,
               |  "dependencies": ["depA", "depB"]
               |}""".stripMargin

    val Left(decodingError) = parser.parse(in).flatMap(_.as[Library]): @unchecked
    assertEquals(decodingError.getMessage, "DecodingFailure at .version: Validation of refined type failed: 7 > 10")
  }

  test("decoder should fail for incorrect Library.dependencies") {
    val in = """{
               |  "name" : "libA",
               |  "version" : 11,
               |  "dependencies": ["depA"]
               |}""".stripMargin

    val Left(decodingError) = parser.parse(in).flatMap(_.as[Library]): @unchecked
    assertEquals(
      decodingError.getMessage,
      "DecodingFailure at .dependencies: Validation of refined type failed: list size doesn't hold predicate: 1 > 1")
  }
