package pl.msitko.refined

import scala.compiletime.ops.boolean._
import scala.compiletime.ops.int._
import scala.compiletime.{codeOf, constValue, erasedValue, error}
import pl.msitko.refined.ValidateExpr
import pl.msitko.refined.ValidateExpr._
import quoted._

object auto:
  implicit inline def mkValidatedInt[V <: Int & Singleton, E <: ValidateExpr](v: V): Refined[V, E] =
    inline ValidateInt.validate[V, E] match
      case "" => Refined.unsafeApply(v)
      case failMsg =>
        inline val wholePredicateMsg = ValidateInt.showPredicate[V, E]
        if wholePredicateMsg == failMsg
          then reportError("Validation failed: " + wholePredicateMsg)
          else reportError("Validation failed: " + wholePredicateMsg + ", predicate failed: " + failMsg)

  implicit inline def mkValidatedString[V <: String & Singleton, E <: ValidateExpr](v: V): Refined[V, E] =
    inline if ValidateString.validate[V, E]
      then Refined.unsafeApply(v)
      else reportError("Validation failed")

  implicit inline def mkValidatedList[T, E <: ValidateExpr](inline v: List[T]): Refined[List[T], E] =
    inline ValidateList.validate[E](v) match
      case ""       => Refined.unsafeApply(v)
      case failMsg: String  => reportError("Validation failed: " + failMsg)

  implicit inline def intLowerThanInference[T <: Int & Singleton, U <: Int & Singleton](v: Int Refined LowerThan[T]): Int Refined LowerThan[U] =
    inline erasedValue[T] < erasedValue[U] match
      case _: true  => Refined.unsafeApply(v.value)
      case _: false => reportError("Cannot be inferred")

  implicit inline def intGreaterThanInference[T <: Int & Singleton, U <: Int & Singleton](v: Int Refined GreaterThan[T]): Int Refined GreaterThan[U] =
    inline erasedValue[T] > erasedValue[U] match
      case _: true  => Refined.unsafeApply(v.value)
      case _: false => reportError("Cannot be inferred")

  // hack around scala.compiletime.error limitation that its argument has to be a literal
  // See: https://github.com/lampepfl/dotty/issues/10315
  private inline def reportError(inline a: String): Nothing = ${ reportErrorCode('a) }

  private def reportErrorCode(a: Expr[String])(using q: Quotes): Nothing =
    q.reflect.report.throwError(a.valueOrError)

//opaque type Refined[Underlying, ValidateExpr] = Underlying
// I couldn't make the following work with opaque type:
// val a: Refined[Int, GreaterThan[10]] = 186
// That worked well with opaque type:
// val a: Int Refined GreaterThan[10] = mkValidatedInt[16, GreaterThan[10]](16)

// T is covariant so `val a: Refined[Int, GreaterThan[10]] = 186` works as well as `val a: Refined[186, GreaterThan[10]] = 186`
// but not sure how important it's that the latter works
final class Refined[+T, P] private (val value: T) extends AnyVal
//trait Refined[+Underlying, ValidateExpr]

object Refined:
  // We cannot simply `implicit inline def mk...(): Refined` because inline and opaque types do not compose
  // Read about it here: https://github.com/lampepfl/dotty/issues/6802
  private [refined] def unsafeApply[T <: Int & Singleton, P <: ValidateExpr](i: T): T Refined P = new Refined[T, P](i)
  private [refined] def unsafeApply[T <: String & Singleton, P <: ValidateExpr](i: T): T Refined P = new Refined[T, P](i)
  private [refined] def unsafeApply[T, P <: ValidateExpr](i: List[T]): List[T] Refined P = new Refined[List[T], P](i)
  implicit def unwrap[T, P](in: Refined[T, P]): T = in.value
