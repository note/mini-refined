package pl.msitko.refined.runtime

import compiletime.{constValue, erasedValue}
import pl.msitko.refined.{compiletime as CT}
import pl.msitko.refined.runtime.ValidateExprInt.{fromCompiletime, And, LowerThan}
import pl.msitko.refined.Refined

class ValidateInt[P <: CT.ValidateExprInt](rtExpr: ValidateExprInt) {

  def apply(v: Int): Either[String, Int Refined P] =
    rtExpr.validate(v) match
      case Some(err) => Left(s"Validation of refined type failed: $err")
      case None      => Right(Refined.unsafeApply[Int, P](v))
}

sealed trait ValidateExprInt {
  def validate(v: Int): Option[String]
}

object ValidateExprInt:

  final case class And(a: ValidateExprInt, b: ValidateExprInt) extends ValidateExprInt:
    def validate(v: Int): Option[String] = a.validate(v).orElse(b.validate(v))

  final case class Or(a: ValidateExprInt, b: ValidateExprInt) extends ValidateExprInt:

    def validate(v: Int): Option[String] = a.validate(v) match
      case Some(err) =>
        b.validate(v) match
          case Some(err2) => Some(s"($err Or $err2)")

  final case class LowerThan(t: Int) extends ValidateExprInt:

    def validate(v: Int): Option[String] =
      if v < t then None
      else Some(s"$v < $t")

  final case class GreaterThan(t: Int) extends ValidateExprInt:

    def validate(v: Int): Option[String] =
      if v > t then None
      else Some(s"$v > $t")

  inline def fromCompiletime[T <: CT.ValidateExprInt]: ValidateExprInt =
    inline erasedValue[T] match
      case _: CT.ValidateExprInt.And[a, b]      => And(fromCompiletime[a], fromCompiletime[b])
      case _: CT.ValidateExprInt.Or[a, b]       => Or(fromCompiletime[a], fromCompiletime[b])
      case _: CT.ValidateExprInt.LowerThan[t]   => LowerThan(constValue[t])
      case _: CT.ValidateExprInt.GreaterThan[t] => GreaterThan(constValue[t])
