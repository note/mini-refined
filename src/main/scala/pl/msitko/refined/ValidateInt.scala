package pl.msitko.refined

import scala.compiletime.ops.boolean._
import scala.compiletime.ops.int._
import scala.compiletime.{codeOf, constValue, erasedValue, error}
import pl.msitko.refined.ValidateExpr.{And, GreaterThan, LowerThan}

object ValidateInt:
  transparent inline def validate[V <: Int with Singleton, E <: ValidateExpr]: Boolean =
    inline erasedValue[E] match
      case _: LowerThan[t] =>
        inline erasedValue[V] < erasedValue[t] match
          case _: true => true
          case _: false =>
            error(
              "Validation failed: " + constValue[ToString[V]] + " < " + constValue[ToString[t]]
            )
      case _: GreaterThan[t] =>
        inline erasedValue[V] > erasedValue[t] match
          case _: true  => true
          case _: false => error("Validation failed: " + constValue[ToString[V]] + " < " + constValue[ToString[t]])
      case _: And[a, b] =>
        inline validate[V, a] match
          case true =>
            inline validate[V, b] match
              case true => true
