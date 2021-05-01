package pl.msitko.refined

import scala.compiletime.ops.boolean._
import scala.compiletime.ops.int._
import scala.compiletime.{codeOf, constValue, erasedValue, error}
import pl.msitko.refined.ValidateExpr
import pl.msitko.refined.ValidateExpr._

object RefinedLift:
  implicit inline def mkValidatedInt[V <: Int with Singleton, E <: ValidateExpr](v: V): Refined[V, E] =
    inline if checkPredicate[V, E]
      then Refined.unsafeApply(v)
      else error("Validation failed")

  transparent inline def checkPredicate[V <: Int with Singleton, E <: ValidateExpr]: Boolean =
    inline erasedValue[E] match
      case _: LowerThan[t] =>
        inline erasedValue[V] < erasedValue[t] match
          case _: true => true
          case _: false =>
            error(
              "Validation failed: " + constValue[ToString[V]] + " < " + constValue[ToString[t]]
            ) // ${erasedValue[V].toString} < ${erasedValue[t].toString}
      case _: GreaterThan[t] =>
        inline erasedValue[V] > erasedValue[t] match
          case _: true  => true
          case _: false => error("Validation failed: " + constValue[ToString[V]] + " < " + constValue[ToString[t]])
      case _: And[a, b] =>
        inline checkPredicate[V, a] match
          case true =>
            inline checkPredicate[V, b] match
              case true => true

opaque type Refined[+Underlying, ValidateExpr] = Underlying
object Refined:
  // We cannot simply `implicit inline def mk...(): Refined` because inline and opaque types do not compose
  // Read about it here: https://github.com/lampepfl/dotty/issues/6802
  private [refined] def unsafeApply[V <: Int with Singleton, E <: ValidateExpr](i: V): V Refined E = i


object Example:
  @main def main(): Unit =
    import RefinedLift._
    val a: Int Refined GreaterThan[10] = RefinedLift.mkValidatedInt[16, GreaterThan[10]](16)
    val b: Int Refined And[GreaterThan[10], LowerThan[20]] = RefinedLift.mkValidatedInt[16, And[GreaterThan[10], LowerThan[20]]](16)
