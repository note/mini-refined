package pl.msitko.refined.runtime

import compiletime.{constValue, erasedValue}
import pl.msitko.refined.compiletime as CT
import pl.msitko.refined.runtime as RT
import pl.msitko.refined.Refined

sealed trait ValidateExprList:
  def validate(v: List[_]): Option[String]

object ValidateExprList:
  final case class Size(sizeIntValidator: RT.ValidateExprInt) extends ValidateExprList:
    def validate(v: List[_]): Option[String] =
      sizeIntValidator.validate(v.size).map(err => s"list size doesn't hold predicate: $err")

  inline def fromCompiletime[T <: CT.ValidateExprList]: ValidateExprList =
    inline erasedValue[T] match
      case _: CT.ValidateExprList.Size[t] => Size(RT.ValidateExprInt.fromCompiletime[t])
