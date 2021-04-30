package pl.msitko.refined

import scala.compiletime.ops.boolean._
import scala.compiletime.ops.int._
import scala.compiletime.{codeOf, constValue, erasedValue, error}
import pl.msitko.refined.ValidateExpr
import pl.msitko.refined.ValidateExpr._

trait Validated[E <: ValidateExpr]

implicit inline def mkValidated[V <: Int with Singleton, E <: ValidateExpr](v: V): Validated[E] =
  inline erasedValue[E] match
    case _: LowerThan[t] =>
      inline erasedValue[V] < erasedValue[t] match
        case _: true => new Validated[E] {}
        case _: false =>
          error(
            "Validation failed: " + constValue[ToString[V]] + " < " + constValue[ToString[t]]
          ) // ${erasedValue[V].toString} < ${erasedValue[t].toString}
    case _: GreaterThan[t] =>
      inline erasedValue[V] > erasedValue[t] match
        case _: true  => new Validated[E] {}
        case _: false => error("Validation failed: " + constValue[ToString[V]] + " < " + constValue[ToString[t]])
    case _: And[a, b] =>
      inline mkValidated[V, a](v) match
        case _: Validated[_] =>
          inline mkValidated[V, b](v) match
            case _: Validated[_] => new Validated[E] {}
