package pl.msitko.refined.compiletime

sealed trait ValidateExprList

object ValidateExprList:
  class And[A <: ValidateExprList, B <: ValidateExprList] extends ValidateExprList
  class Or[A <: ValidateExprList, B <: ValidateExprList]  extends ValidateExprList
  class Size[T <: ValidateExprInt]                        extends ValidateExprList
