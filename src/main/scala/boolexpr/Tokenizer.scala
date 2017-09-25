package boolexpr

class Tokenizer {
  private var data: String = ""
  private var pos: Int = 0

  def this(s: String) = {
    this()
    this.data = s.trim
    this.pos = 0
  }

  override def toString: String = { s"StringTokenizer(data:${this.data}, pos:${this.pos})" }

  val usableChars = "@#$%^+-=_/.:;`'\"><[]\\*?~abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
  def isUsableChar(c: Char): Boolean = { c.isLetterOrDigit || c == '_' }
  def isParen(c: Char): Boolean = { c == '(' || c == ')' }
  def isBinOp(c: Char): Boolean = { c == '&' || c == '|' }
  def isUnaryOp(c: Char): Boolean = { c == '!' }

  def nextPos: Int = {
    val s = this.data
    val pos = this.pos
    var c:Char = '\u0000'
    var endPos: Int = pos + 1
    val insideOfVar = s(pos).isLetterOrDigit
    var done = false

    while (endPos < s.length && !done) {
      c = s(endPos);
      if (isUsableChar(c)) {
        if (!insideOfVar) {
          done = true; // break
        }
      } else if (isParen(c) || isBinOp(c) || isUnaryOp(c)) {
        done = true;
      } else if (!c.isWhitespace) {
        throw new Error(s"Tokenizer.nextPos: expected alphanum, parens, or binop, found '${c}' for ${this}")
      }
      if (!done) { endPos += 1 }
    }
    endPos
  }

  def getPos: Int = { this.pos }

  def hasMoreTokens: Boolean = { this.pos < this.data.length  }

  def peek: String = {
    this.data(this.pos) match {
      case '(' => Tokenizer.LPAREN_TOK
      case ')' => Tokenizer.RPAREN_TOK
      case '&' => Tokenizer.AND_TOK
      case '|' => Tokenizer.OR_TOK
      case _ =>
        val endPos = this.nextPos
        this.data.substring(this.pos, endPos).trim()
    }
  }

  def consume(s: String): Unit = {
    val endPos = this.nextPos
    val expected = this.data.substring(this.pos, endPos).trim
    if (expected == s) {
      this.pos = endPos;
    } else {
      throw new Exception(s"Tokenizer.consume: expected ${expected} but saw ${s} at ${this}")
    }
  }
}

object Tokenizer {
  private val LPAREN_TOK: String = "("
  private val RPAREN_TOK: String = ")"
  private val AND_TOK: String = "&"
  private val OR_TOK: String = "|"
}
