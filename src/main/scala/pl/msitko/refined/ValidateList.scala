package pl.msitko.refined

import pl.msitko.refined.ValidateExpr.ListSizeConstraint
import pl.msitko.refined.macros.ListMacros

import scala.compiletime.erasedValue
import scala.quoted.Exprs

object ValidateList:
//  transparent inline def validate[E <: ValidateExpr](inline in: List[_]): Boolean =
//    inline erasedValue[E] match
//      case _: ListSizeConstraint[t] =>
//        ValidateInt.validate[ListMacros.listSize(in), t]
  val a = 33

