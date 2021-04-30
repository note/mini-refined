package pl.msitko.refined

import scala.compiletime.ops.boolean._
import scala.compiletime.ops.int._
import scala.compiletime.{codeOf, constValue, erasedValue, error}
import pl.msitko.refined.ValidateExpr
import pl.msitko.refined.ValidateExpr._

object RefinedLift:
  implicit inline def mkValidatedInt[V <: Int with Singleton, E <: ValidateExpr](v: V): Refined[V, E] =
    inline erasedValue[E] match
      case _: LowerThan[t] =>
        inline erasedValue[V] < erasedValue[t] match
          case _: true => Refined.unsafeApply(v)
          case _: false =>
            error(
              "Validation failed: " + constValue[ToString[V]] + " < " + constValue[ToString[t]]
            ) // ${erasedValue[V].toString} < ${erasedValue[t].toString}
      case _: GreaterThan[t] =>
        inline erasedValue[V] > erasedValue[t] match
          case _: true  => Refined.unsafeApply(v)
          case _: false => error("Validation failed: " + constValue[ToString[V]] + " < " + constValue[ToString[t]])
      case _: And[a, b] =>
        inline mkValidatedInt[V, a](v) match
          case _: Refined[_, _] =>
            inline mkValidatedInt[V, b](v) match
              case _: Refined[_, _] => Refined.unsafeApply(v)

opaque type Refined[+Underlying, ValidateExpr] = Underlying
object Refined:
  // We cannot simply `implicit inline def mk...(): Refined` because inline and opaque types do not compose
  // Read about it here: https://github.com/lampepfl/dotty/issues/6802
  private [refined] def unsafeApply[V <: Int with Singleton, E <: ValidateExpr](i: Int): V Refined E = ???


object Example:
  @main def main(): Unit =
    import RefinedLift._
    val a: Int Refined GreaterThan[10] = RefinedLift.mkValidatedInt[16, GreaterThan[10]](16)
