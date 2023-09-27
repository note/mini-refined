package pl.msitko.refined.runtime

import compiletime.{constValue, erasedValue}
import pl.msitko.refined.compiletime as CT

sealed trait ValidateExprString:
  def validate(v: String): Option[String]

object ValidateExprString:

  final case class And(a: ValidateExprString, b: ValidateExprString) extends ValidateExprString:
    def validate(v: String): Option[String] = a.validate(v).orElse(b.validate(v))

  final case class Or(a: ValidateExprString, b: ValidateExprString) extends ValidateExprString:
    def validate(v: String): Option[String] = a.validate(v) match
      case Some(err) =>
        b.validate(v) match
          case Some(err2) => Some(s"($err Or $err2)")
          case None       => None
      case None =>
        None

  final case class StartsWith(t: String) extends ValidateExprString:
    def validate(v: String): Option[String] =
      if v.startsWith(t) then None
      else Some(s"$v.startWith($t)")

  final case class EndsWith(t: String) extends ValidateExprString:
    def validate(v: String): Option[String] =
      if v.endsWith(t) then None
      else Some(s"$v.endsWith($t)")

  inline def fromCompiletime[T <: CT.ValidateExprString]: ValidateExprString =
    inline erasedValue[T] match
      case _: CT.ValidateExprString.And[a, b]     => And(fromCompiletime[a], fromCompiletime[b])
      case _: CT.ValidateExprString.Or[a, b]      => Or(fromCompiletime[a], fromCompiletime[b])
      case _: CT.ValidateExprString.StartsWith[t] => StartsWith(constValue[t])
      case _: CT.ValidateExprString.EndsWith[t]   => EndsWith(constValue[t])
