package boolexpr

import scala.reflect.ClassTag
import scala.collection.mutable.ArrayBuffer

/* 
 Description of recursive algorithm to convert X to extended CNF form,
 using a pseudo Scala notation for pattern matching. Worst case exponential
 time and space, so far well behaved in practice.

 Historical notes: This algorithm was derived in a rather haphazard way,
 the original, naive algorithm generated CNF and DNF from truth tables
 and so always incurred an exponential hit in the number of variables, and became
 unusable for more than 15 or 20 variables. Translation of

 https://github.com/bpodgursky/jbool_expressions

 into JS yielded a more promising approach, and is responsible for the use
 of classes. It applied a set of rules to a tree until it converged to a fixed
 point. That seemed to work but it was difficult to analyze.

 Analysis: This algorithm is known to be exponential in the worst case, for example
 in the case of converting an expression in DNF form to CNF.

 NB: We can easily extend this algorithm to handle remaining boolean operators
 implication, equivalence, and xor, etc.
*/

sealed trait BoolExpr {}

case object True extends BoolExpr
case object False extends BoolExpr
final case class Var(name: String) extends BoolExpr
final case class Not(expr: BoolExpr) extends BoolExpr
final case class And(exprs: Array[BoolExpr]) extends BoolExpr
final case class Or(exprs: Array[BoolExpr]) extends BoolExpr
// final case class Xor(l: BoolExpr, r: BoolExpr) extends BoolExpr
// final case class Implies(l: BoolExpr, r: BoolExpr) extends BoolExpr
// final case class Equivalent(l: BoolExpr, r: BoolExpr) extends BoolExpr

object BoolExpr {
  def show(e:BoolExpr): String = {
    e match {
      case True => "true"
      case False => "false"
      case Var(n) => s"Var(${n})"
      case Not(e) => s"Not(${show(e)})"
      case And(exprs) =>
        val argString = exprs.map(show).mkString(", ")
        s"And(${argString})"
      case Or(exprs) =>
        val argString = exprs.map(show).mkString(", ")
        s"Or(${argString})"
    }
  }

  def children(x:BoolExpr): Array[BoolExpr] = {
    x match {
      case True => Array(True)
      case False => Array(False)
      case Var(n) => Array(x)
      case Not(e) => Array(e)
      case And(exprs) => exprs
      case Or(exprs) => exprs
    }
  }

  def makeProdNode(expr: BoolExpr): BoolExpr = {
    expr match {
      case Var(n) => And(Array(expr))
      case _ => expr
    }
  }

  def makeSumNode(expr: BoolExpr): BoolExpr = {
    expr match {
      case Var(n) => Or(Array(expr))
      case _ => expr
    }
  }

  def inflateCNF(expr: BoolExpr): BoolExpr = {
    expr match {
      case Var(n) => And(Array(Or(Array(expr))))
      case And(exprs) => And(exprs.map((e) => makeSumNode(e)))
      case Or(exprs) => And(Array(expr))
      case _ => expr
    }
  }

  def inflateDNF(expr: BoolExpr): BoolExpr = {
    expr match {
      case Var(n) => Or(Array(And(Array(expr))))
      case And(exprs) => Or(Array(expr))
      case Or(exprs) => Or(exprs.map((e) => makeProdNode(e)))
      case _ => expr
    }
  }

  def illegalArg(msg: String): Unit = {
    throw new IllegalArgumentException(msg);
  }

  def makeDisjunction(a: Array[BoolExpr]): BoolExpr = {
    var buf = new ArrayBuffer[BoolExpr](a.length)
    for (expr <- a) {
      expr match {
        case Var(n) => buf.append(expr)
        case Or(exprs) => 
          for (e <- exprs) {
            buf.append(e)
          }
        case _ => illegalArg("makeDisjunction: expected Var or Or");
      }
    }
    Or(buf.toArray)
  }

  def makeConjunction(a: Array[BoolExpr]): BoolExpr = {
    var buf = new ArrayBuffer[BoolExpr](a.length)
    for (expr <- a) {
      expr match {
        case Var(n) => buf.append(expr)
        case And(exprs) =>
          for (e <- exprs) {
            buf.append(e)
          }
        case _ => illegalArg("makeConjunction: expected Var or And")
      }
    }
  
    And(buf.toArray)
  }

  def nextTuple(indices: Array[Int], sizes: Array[Int]): Boolean = {
    var changed = false
    var i: Int = indices.length - 1
    while (i >= 0 && !changed) {
      if (indices(i) < sizes(i) - 1) {
        indices(i) += 1
        changed = true
      } else {
        indices(i) = 0
      }
      i -= 1
    }
    changed
  }

  def cartesianProduct[T:ClassTag](aa: Array[Array[T]]): Array[Array[T]] = {
    var indices = aa.map((a) => 0)
    val sizes = aa.map((a) => a.length)
    var result = new Array[Array[T]](sizes.reduceLeft((x,y) => x * y))
    var pos = 0
    do {
      var elt = new Array[T](indices.length)
      for (i <- 0 until indices.length) {
        elt(i) = aa(i)(indices(i))
      }
      result(pos) = elt
      pos += 1
    } while (nextTuple(indices, sizes))

    result
  }

  def toCNF(x: BoolExpr): BoolExpr = {
    x match {
      case And(v) =>
        val newChildren = v.map(toCNF)
        And(newChildren.flatMap(children)) // 'And' distributes, stays a CNF
      case Or(v) =>
        // toCNF(v[0]) is v00 ^ v01 ^ ... v0I
        // toCNF(v[1]) is v10 ^ v11 ^ ... v1J
        // ...
        // toCNF(v[M]) is vM0 ^ vM1 ^ ... v1K
        // Each vij is a disjunction of literals
        //
        // Notice (X1 ^ X2) v (Y1 ^ Y2) <=> (X1 v Y1) ^ (X1 v Y2) ^ (X2 v Y1) ^ (X2 v Y2)
        // and that more generally, Or(v) <=> And(cartesianProduct(v.map(toCNF)))
        val newChildren = v.map(toCNF)
        val disjuncts = cartesianProduct(newChildren.map(children))
        And(disjuncts.map((exprs) => makeDisjunction(exprs)))
      case Not(v) =>
        v match {
          case True => False
          case False => True
          case Var(n) => x
          case Not(e) => toCNF(e) // Double negation
          case And(exprs) => toCNF(Or(exprs.map((e) => Not(e)))) // DeMorgan
          case Or(exprs)  => toCNF(And(exprs.map((e) => Not(e)))) // DeMorgan
        }
      case _ => x
    }
  }

  def toDNF(expr: BoolExpr): BoolExpr = {
    expr match {
      case Or(arr) =>
        val newChildren = arr.map(toDNF)
        Or(newChildren.flatMap(children)) // 'And' distributes, stays a CNF
      case And(arr) =>
        val newChildren = arr.map(toDNF)
        val conjuncts = cartesianProduct(newChildren.map(children))
        Or(conjuncts.map((exprs) => makeConjunction(exprs)))
      case Not(e) => // Not strictly necessary since our expressions are monotonic
        e match {
          case True => False
          case False => True
          case Var(n) => expr
          case Not(e) => toDNF(e) // Double negation
          case And(exprs) => toDNF(Or(exprs.map((e) => Not(e)))) // DeMorgan
          case Or(exprs)  => toDNF(And(exprs.map((e) => Not(e)))) // DeMorgan
        }
      case _ => expr
    }
  }

  def normalizedForms(expr: BoolExpr): (BoolExpr, BoolExpr) = {
    (inflateDNF(toDNF(expr)), inflateCNF(toCNF(expr)))
  }

  def flatten(expr: BoolExpr): BoolExpr = {
    expr
  }

  def simplify(expr: BoolExpr): BoolExpr = {
    expr
  }
}
