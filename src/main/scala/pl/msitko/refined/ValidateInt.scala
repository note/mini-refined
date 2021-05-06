package pl.msitko.refined

import scala.compiletime.ops.boolean._
import scala.compiletime.ops.int._
import scala.compiletime.{codeOf, constValue, erasedValue, error}
import pl.msitko.refined.ValidateExpr.{And, GreaterThan, LowerThan, Or}

object ValidateInt:
  transparent inline def validate[V <: Int with Singleton, E <: ValidateExpr]: Option[String] =
    inline erasedValue[E] match
      case _: LowerThan[t] =>
        inline erasedValue[V] < erasedValue[t] match
          case _: true => None
          case _: false => Some(showPredicate[V, E])
      case _: GreaterThan[t] =>
        inline erasedValue[V] > erasedValue[t] match
          case _: true  => None
          case _: false => Some(showPredicate[V, E])
      case _: And[a, b] =>
        inline validate[V, a] match
          case None =>
            inline validate[V, b] match
              case None => None
              case Some(msg) => Some("abc 2")
          case Some(msg) => Some(msg)
      case _: Or[a, b] =>
        inline validate[V, a] match
          case None => None
          case Some(msg) =>
            inline validate[V, b] match
              case None => None
              case Some(msg) =>
                Some(showPredicate[V, E])

  transparent inline def showPredicate[V <: Int with Singleton, E <: ValidateExpr]: String =
    inline erasedValue[E] match
      case _: LowerThan[t] =>
        constValue[ToString[V]] + " < " + constValue[ToString[t]]
      case _: GreaterThan[t] =>
        constValue[ToString[V]] + " > " + constValue[ToString[t]]
      case _: And[a, b] =>
        inline val aMsg = showPredicate[V, a]
        inline val bMsg = showPredicate[V, b]
        "(" + aMsg + " And " + bMsg + ")"
      case _: Or[a, b] =>
        inline val aMsg = showPredicate[V, a]
        inline val bMsg = showPredicate[V, b]
        "(" + aMsg + " Or " + bMsg + ")"
