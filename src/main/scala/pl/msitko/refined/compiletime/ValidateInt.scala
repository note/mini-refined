package pl.msitko.refined.compiletime

import scala.compiletime.ops.any.ToString
import scala.compiletime.ops.boolean._
import scala.compiletime.ops.int._
import scala.compiletime.{codeOf, constValue, erasedValue, error}
import pl.msitko.refined.compiletime.ValidateExprInt
import pl.msitko.refined.compiletime.ValidateExprInt.{And, GreaterThan, LowerThan, Or}

object ValidateInt:

  transparent inline def validate[V <: Int & Singleton, E <: ValidateExprInt]: String | Null =
    validateV[E](constValue[V], constValue[ToString[V]])

  // It used to have Option[String] as a return type but there were some glitches when trying to report errors:
  // Expected a known value.
  // [error]
  // [error]      The value of: "Validation failedd: (25 > 10 And 25 < 20)".+(failMsg)
  // [error]      could not be extracted using scala.quoted.FromExpr$PrimitiveFromExpr@60d33381
  transparent inline def validateV[E <: ValidateExprInt](v: Int, asString: String): String | Null =
    inline erasedValue[E] match
      case _: LowerThan[t] =>
        inline v < constValue[t] match
          case _: true  => null
          case _: false => showPredicateV[E](asString)
      case _: GreaterThan[t] =>
        inline v > constValue[t] match
          case _: true  => null
          case _: false => showPredicateV[E](asString)
      case _: And[a, b] =>
        inline validateV[a](v, asString) match
          case null =>
            validateV[b](v, asString)
          case _ =>
            validateV[a](v, asString)
      case _: Or[a, b] =>
        inline validateV[a](v, asString) match
          case null => null
          case msg =>
            inline validateV[b](v, asString) match
              case null => null
              case msg =>
                showPredicateV[E](asString)

  transparent inline def showPredicate[V <: Int & Singleton, E <: ValidateExprInt]: String =
    showPredicateV[E](constValue[ToString[V]])

  transparent inline def showPredicateV[E <: ValidateExprInt](asString: String): String =
    inline erasedValue[E] match
      case _: LowerThan[t] =>
        asString + " < " + constValue[ToString[t]]
      case _: GreaterThan[t] =>
        asString + " > " + constValue[ToString[t]]
      case _: And[a, b] =>
        inline val aMsg = showPredicateV[a](asString)
        inline val bMsg = showPredicateV[b](asString)
        "(" + aMsg + " And " + bMsg + ")"
      case _: Or[a, b] =>
        inline val aMsg = showPredicateV[a](asString)
        inline val bMsg = showPredicateV[b](asString)
        "(" + aMsg + " Or " + bMsg + ")"
