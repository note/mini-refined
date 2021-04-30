package pl.msitko.refined

sealed trait ValidateExpr

object ValidateExpr:
  class And[A <: ValidateExpr, B <: ValidateExpr] extends ValidateExpr
  class Or[A <: ValidateExpr, B <: ValidateExpr]  extends ValidateExpr
  class Leaf                                      extends ValidateExpr
  class LowerThan[T <: Int with Singleton]        extends Leaf
  class GreaterThan[T <: Int with Singleton]      extends Leaf
