package boolexpr

import org.scalatest._

class BoolExprSpec extends FlatSpec with Matchers {
  "A variable " should "be the same as it's own normalized form" in {
    val singleVar = "X"
    val expr = Parser.parseString(singleVar)
    val (sop, pos) = BoolExpr.normalizedForms(expr)
    val sopString = BoolExpr.show(sop)
    val posString = BoolExpr.show(pos)
    val cnfExpr = BoolExpr.show(And(Array(Or(Array(expr)))))
    val dnfExpr = BoolExpr.show(Or(Array(And(Array(expr)))))
    dnfExpr should be (sopString)
    cnfExpr should be (posString)
  }
}
