package pl.msitko.refined

import pl.msitko.refined.ValidateExpr.ListSizeConstraint
import quoted.*
import scala.compiletime.erasedValue
import scala.quoted.Exprs

object ValidateList:
//  transparent inline def validate[E <: ValidateExpr](inline in: List[_]): Boolean =
//    inline erasedValue[E] match
//      case _: ListSizeConstraint[t] =>
  val a = 45

