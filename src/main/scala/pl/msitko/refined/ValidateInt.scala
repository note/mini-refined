package pl.msitko.refined

import scala.compiletime.ops.boolean._
import scala.compiletime.ops.int._
import scala.compiletime.{codeOf, constValue, erasedValue, error}
import pl.msitko.refined.ValidateExpr.{And, GreaterThan, LowerThan, Or}

object ValidateInt:
  transparent inline def validate[V <: Int & Singleton, E <: ValidateExpr]: String =
    validateV[E](constValue[V], constValue[ToString[V]])

  // It used to have Option[String] as a return type but there were some glitches when trying to report errors:
  // Expected a known value.
  // [error]
  // [error]      The value of: "Validation failedd: (25 > 10 And 25 < 20)".+(failMsg)
  // [error]      could not be extracted using scala.quoted.FromExpr$PrimitiveFromExpr@60d33381
  transparent inline def validateV[E <: ValidateExpr](v: Int, asString: String): String =
    inline erasedValue[E] match
      case _: LowerThan[t] =>
        inline v < constValue[t] match
          case _: true => ""
          case _: false => showPredicateV[E](v, asString)
      case _: GreaterThan[t] =>
        inline v > constValue[t] match
          case _: true  => ""
          case _: false => showPredicateV[E](v, asString)
      case _: And[a, b] =>
        inline validateV[a](v, asString) match
          case "" =>
            validateV[b](v, asString)
          case msg =>
            println("hello!")
            msg
      case _: Or[a, b] =>
        inline validateV[a](v, asString) match
          case "" => ""
          case msg =>
            inline validateV[b](v, asString) match
              case "" => ""
              case msg =>
                showPredicateV[E](v, asString)

  transparent inline def showPredicate[V <: Int & Singleton, E <: ValidateExpr]: String =
    showPredicateV[E](constValue[V], constValue[ToString[V]])

  transparent inline def showPredicateV[E <: ValidateExpr](v: Int, asString: String): String =
    inline erasedValue[E] match
      case _: LowerThan[t] =>
        asString + " < " + constValue[ToString[t]]
      case _: GreaterThan[t] =>
        asString + " > " + constValue[ToString[t]]
      case _: And[a, b] =>
        inline val aMsg = showPredicateV[a](v, asString)
        inline val bMsg = showPredicateV[b](v, asString)
        "(" + aMsg + " And " + bMsg + ")"
      case _: Or[a, b] =>
        inline val aMsg = showPredicateV[a](v, asString)
        inline val bMsg = showPredicateV[b](v, asString)
        "(" + aMsg + " Or " + bMsg + ")"
