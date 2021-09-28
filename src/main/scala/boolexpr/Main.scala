package boolexpr

object Main {
  val strings: Array[String] = Array(
    "A",
    "B",
    "A&B",
    "A|B",
    "A|C&B",
    "A&(C|B)",
    "((A & C) | (A & B))",
    "A|B|(C&D)",
    "(B|C&D)",
    "A&(B|C&D)",
    "(( A & ( B | C)) | ( D & E & F0))",
    "((natera&(child|brother))|(mother&HNR&panorama))",
    "((lisReference816658&productPANORAMA&maternalAge23)|(maternalAge36&lisReference817491&productPANORAMA_NO_MOM)|(lisReference818202|lisReference816658|lisReference817491))",
    "((a&b&c)|(d&e&f)|(h|i|e))",
    "(((prop0)&(prop1)&(prop2))|(prop3&prop4&prop5)|((prop6)|(prop0)|(prop4)))",
    "((prop70790|prop26910|prop69760|prop46750|prop77940)&prop9090&(prop1010|prop80690|prop52640|prop95530|prop7300))",
    "((A|B|C|D|E)&F0&(G|H|I|J|K))",
    "((Meta|TestManifest|MetricsRollup|AlleleCounts)&(HetrateResult|AlleleCounts))",
    "(X00|X01)&(X02|X03)&(X04|X05)&(X06|X07)&(X08|X09)&(X10|X11)&(X12|X13)",
    "(X00&X01)|(X02&X03)|(X04&X05)|(X06&X07)|(X08&X09)|(X10&X11)|(X12&X13)|(X14&X15)|(X16&X17)|(X18&X19)|(X20&X21)",
    //p 24 variables
    "((prop1310|prop86540|prop93720|prop33550|prop40130)&prop59770&(prop43340|prop10410|prop19220|prop91500|prop47650|(prop85400&prop54480&prop4110&prop24580&prop62260&prop26990&prop84280&prop35590&prop69520&prop56870&prop80080&prop84220&prop62510)))",
    // 38 variables
    "((prop1310|prop86540|prop93720|prop33550|prop40130)&prop59770&(prop43340|prop10410|prop19220|prop91500|prop47650|(prop85400&prop54480&prop4110&prop24580&prop62260&prop26990&prop84280&prop35590&prop69520&prop56870&prop80080&prop84220&prop62510&prop42660&prop54710&prop11130&prop53230&prop94080&prop96830&prop69990&prop99060&prop35580&prop1980&prop6090&prop95430&prop74340&prop99710)))"
  )
  def main(args: Array[String]): Unit = {
    for (s <- strings) {
      val expr = Parser.parseString(s)
      val (sop, pos) = BoolExpr.normalizedForms(expr)
      println(s"expr=${s}")
      println(s"POS=${BoolExpr.show(pos)}")
      println(s"SOP=${BoolExpr.show(sop)}")
      println(s"--------------------------------------------------------------------------------")
    }
  }
}
