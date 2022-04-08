package pl.msitko.refined.compiletime

import pl.msitko.refined.compiletime.ValidateExprList
import pl.msitko.refined.compiletime.ValidateExprList._
import pl.msitko.refined.macros.ListMacros

import scala.compiletime.erasedValue
import scala.quoted.Exprs

object ValidateList:
  transparent inline def validate[E <: ValidateExprList](inline in: List[_]): String | Null =
    inline erasedValue[E] match
      case _: Size[t] =>
        inline ValidateInt.validateV[t](ListMacros.listSize(in), ListMacros.listSizeString(in)) match
          case null    => null
          case failMsg => "list size doesn't hold predicate: " + failMsg
      case _ =>
        "Couldn't be validated as List"
