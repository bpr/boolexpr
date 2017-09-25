package boolexpr

object Parser {
  // BNF of string syntax for boolean exprressions
  // <expr>  ::=  <term> | <term> "&" <expr> | <term> "|" <expr>
  // <term>  ::=  <var>  |  <literal> | "(" <expr> ")" | "!" <expr>
  // <var>  ::= (a-zA-Z)+(a-zA-Z0-9)*
  // <literal> ::= "T" | "F" #UNUSED

  // Parse expression string by recursive descent to yield an AST

  // We don't support literals for this application. Add support by describing
  // what a boolean literal is (e.g., "true" and "false") in this function
  def isLitString(s: String): Boolean = {
    s == "true" || s == "false"
  }

  val tokenChars = "@#$%^-=_/.:;><[]?~abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"

  def isTokenString(s: String): Boolean = {
    s.foldLeft(true)((accum, c) => accum && (tokenChars contains c))
  }

  def litOfString(s: String): BoolExpr = {
    s match {
      case "true" => True
      case "false" => False
      case _ =>
        throw new Error(s"litOfString: '${s}' is not a valid literal string")
    }
  }

  def parseString(s: String): BoolExpr = {
    val tokens = new Tokenizer(s);
    val result = parseExpr(tokens);
    return result;
  }

  def parseExpr(tokens: Tokenizer): BoolExpr = {
    val term = parseTerm(tokens)
    if (tokens.hasMoreTokens) {
      val nextToken = tokens.peek
      nextToken match {
        case "&" => // "&" <expr>
          tokens.consume("&")
          And(Array(term, parseExpr(tokens)))
        case "|" =>  // "|" <expr>
          tokens.consume("|")
          Or(Array(term, parseExpr(tokens)))
        case _ => // epsilon
          term
      }
    } else {
      term
    }
  }

  def parseTerm(tokens: Tokenizer): BoolExpr = {
    val tokenString = tokens.peek
    tokenString match {
      case "!" => // "!" <expr>
        tokens.consume("!")
        val result = parseExpr(tokens)
        Not(result)
      case "(" => //  "(" <expr> ")"
        tokens.consume("(")
        val result = parseExpr(tokens)
        tokens.consume(")")
        result
      case _ =>
        if (isTokenString(tokenString)) {
          tokens.consume(tokenString);
          if (isLitString(tokenString)) {
            litOfString(tokenString);
          } else {
            Var(tokenString)
          }
        } else {
          throw new Error(s"parseTerm: unexpected token ${tokenString} at ${tokens}")
        }
    }
  }
}

