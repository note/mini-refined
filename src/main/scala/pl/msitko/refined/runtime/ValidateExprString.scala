package pl.msitko.refined.runtime

import compiletime.{constValue, erasedValue}
import pl.msitko.refined.{compiletime as CT}
import pl.msitko.refined.Refined

private[refined] class ValidateString[P <: CT.ValidateExprString](rtExpr: ValidateExprString):
  def appply(v: String): Either[String, String Refined P] =
    rtExpr.validate(v) match
      case Some(err) => Left(s"Validation of refined type failed: $err")
      case None      => Right(Refined.unsafeApply[String, P](v))

sealed private[refined] trait ValidateExprString:
  def validate(v: String): Option[String]

private[refined] object ValidateExprString:
  // TODO: remove c&p from ValidateExprInt
  final case class And(a: ValidateExprString, b: ValidateExprString) extends ValidateExprString:
    def validate(v: String): Option[String] = a.validate(v).orElse(b.validate(v))

  final case class Or(a: ValidateExprString, b: ValidateExprString) extends ValidateExprString:
    def validate(v: String): Option[String] = a.validate(v) match
      case Some(err) =>
        b.validate(v).map(err2 => s"($err Or $err2)")
      case None =>
        None

  final case class StartsWith(p: String) extends ValidateExprString:
    def validate(v: String): Option[String] =
      if v.startsWith(p) then None
      else Some(s"$v.startsWith($p)")

  final case class EndsWith(p: String) extends ValidateExprString:
    def validate(v: String): Option[String] =
      if v.endsWith(p) then None
      else Some(s"$v.endsWith($p)")

  inline def fromCompiletime[T <: CT.ValidateExprString]: ValidateExprString =
    inline erasedValue[T] match
      case _: CT.ValidateExprString.And[a, b]     => And(fromCompiletime[a], fromCompiletime[b])
      case _: CT.ValidateExprString.Or[a, b]      => Or(fromCompiletime[a], fromCompiletime[b])
      case _: CT.ValidateExprString.StartsWith[t] => StartsWith(constValue[t])
      case _: CT.ValidateExprString.EndsWith[t]   => EndsWith(constValue[t])
