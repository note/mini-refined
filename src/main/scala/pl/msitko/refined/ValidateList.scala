package pl.msitko.refined

import pl.msitko.refined.ValidateExpr.Size
import pl.msitko.refined.macros.ListMacros

import scala.compiletime.erasedValue
import scala.quoted.Exprs

object ValidateList:
  transparent inline def validate[E <: ValidateExpr](inline in: List[_]): String =
    inline erasedValue[E] match
      case _: Size[t] =>
        inline ValidateInt.validateV[t](ListMacros.listSize(in), ListMacros.listSizeString(in)) match
          case ""       => ""
          case failMsg  => "list size doesn't hold predicate: " + failMsg
      case _ =>
        "Couldn't be validated as List"
