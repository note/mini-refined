package pl.msitko.refined

import scala.compiletime.ops.boolean._
import scala.compiletime.ops.int._
import scala.compiletime.{codeOf, constValue, erasedValue, error}
import pl.msitko.refined.ValidateExpr
import pl.msitko.refined.ValidateExpr._
import quoted.{Expr, Quotes}

object auto:
  implicit inline def mkValidatedInt[V <: Int with Singleton, E <: ValidateExpr](v: V): Refined[V, E] =
    inline if ValidateInt.validate[V, E]
      then Refined.unsafeApply(v)
      else error("Validation failed")

  implicit inline def mkValidatedString[V <: String with Singleton, E <: ValidateExpr](v: V): Refined[V, E] =
    inline if ValidateString.validate[V, E]
      then Refined.unsafeApply(v)
      else error("Validation failed")

//opaque type Refined[+Underlying, ValidateExpr] = Underlying
trait Refined[+Underlying, ValidateExpr]

object Refined:
  // We cannot simply `implicit inline def mk...(): Refined` because inline and opaque types do not compose
  // Read about it here: https://github.com/lampepfl/dotty/issues/6802
  private [refined] def unsafeApply[V <: Int with Singleton, E <: ValidateExpr](i: V): V Refined E = new Refined[V, E] {}
  private [refined] def unsafeApply[V <: String with Singleton, E <: ValidateExpr](i: V): V Refined E = new Refined[V, E] {}
