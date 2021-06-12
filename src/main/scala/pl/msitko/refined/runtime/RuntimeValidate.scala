package pl.msitko.refined.runtime

import pl.msitko.refined.Refined
import pl.msitko.refined.{compiletime as CT}

private[refined] class Validate[P]:
  def apply[T](v: T)(using ev: RuntimeValidate[T, P]): Either[String, T Refined P] =
    ev.validate(v)

trait RuntimeValidate[T, P]:
  def validate(v: T): Either[String, T Refined P]

object RuntimeValidate:
  implicit inline def intInstance[P <: CT.ValidateExprInt]: RuntimeValidate[Int, P] =
    val validator = ValidateExprInt.fromCompiletime[P]
    new RuntimeValidate[Int, P]:
      def validate(v: Int): Either[String, Int Refined P] =
        validator.validate(v) match
          case Some(err) => Left(s"Validation of refined type failed: $err")
          case None      => Right(Refined.unsafeApply[Int, P](v))
          
  implicit inline def stringInstance[P <: CT.ValidateExprString]: RuntimeValidate[String, P] =
    val validator = ValidateExprString.fromCompiletime[P]
    new RuntimeValidate[String, P]:
      def validate(v: String): Either[String, String Refined P] =
        validator.validate(v) match
          case Some(err) => Left(s"Validation of refined type failed: $err")
          case None => Right(Refined.unsafeApply[String, P](v))
