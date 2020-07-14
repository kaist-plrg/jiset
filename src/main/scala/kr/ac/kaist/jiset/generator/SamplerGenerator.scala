package kr.ac.kaist.jiset.generator

import java.io.PrintWriter
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._

object SamplerGenerator {
  def apply(packageName: String, modelDir: String, grammar: Grammar, debug: Boolean = false): Unit = {
    val nf = getPrintWriter(s"$modelDir/Sampler.scala")
    val Grammar(lexProds, prods) = grammar
    val targetProds = prods.filter(!_.lhs.isModuleNT)

    def getSampler(prod: Production): Unit = {
      val Production(lhs, rhsList) = prod
      val Lhs(name, rawParams) = lhs
      val params = rawParams.map("p" + _)
      val paramsStr = params.map(", " + _ + ": Boolean").mkString("")
      nf.println(s"""  def $name(depth: Int$paramsStr): $name = {""")
      nf.println(s"""    var candidates: Vector[() => $name] = Vector()""")
      nf.println(s"""    val rhsDepth = counter.${name}(${params.mkString(", ")}).rhsDepth""")
      rhsList.zipWithIndex.foreach {
        case (rhs, i) => getCandidates(name, params, rhs, i)
      }
      nf.println(s"""    choose(candidates)""")
      nf.println(s"""  }""")
    }

    def getCandidates(name: String, bs: List[String], rhs: Rhs, idx: Int): Unit = {
      if (!rhs.isModuleNT) {
        val Rhs(tokens, cond) = rhs
        val nextDepth = "depth" + (if (rhs.isSingleNT) "" else " - 1")
        val astName = s"$name$idx"
        val params = tokens.flatMap(tokenSampler(nextDepth, _)).map(_ + ", ").mkString
        val bsstr = bs.mkString(", ")
        var sampler = s"$astName(${params}List($bsstr))"
        nf.print("    ")
        if (cond != "") nf.print(s"if ($cond) ")
        nf.println(s"""rhsDepth($idx).collect { case d if depth >= d => candidates :+= { () => $sampler } }""")
      }
    }

    lazy val paramMap: Map[String, List[String]] =
      prods.map(prod => prod.lhs.name -> prod.lhs.params).toMap
    def getArgs(name: String, args: List[String]): List[String] = {
      val params = paramMap.getOrElse(name, Nil)
      val argMap = args.foldLeft(Map[String, String]()) {
        case (m, arg) =>
          val dropped = arg.drop(1)
          m + {
            if (arg.startsWith("~")) dropped -> "false"
            else if (arg.startsWith("+")) dropped -> "true"
            else if (arg.startsWith("?")) dropped -> ("p" + dropped)
            else dropped -> "false"
          }
      }
      params.map(p => argMap.getOrElse(p, "false"))
    }
    def tokenSampler(nextDepth: String, token: Token): Option[String] = token match {
      case NonTerminal(name, params, optional) =>
        val args = getArgs(name, params)
        val argsStr = args.map(", " + _).mkString("")
        val sampler = s"$name($nextDepth$argsStr)"
        Some(if (optional) {
          val depth = s"counter.$name(${args.mkString(", ")}).depth"
          s"opt($nextDepth >= $depth, $sampler)"
        } else sampler)
      case ButNot(base, _) => tokenSampler(nextDepth, base)
      case _ => None
    }

    nf.println(s"""package $packageName.model""")
    nf.println
    nf.println(s"""import $packageName.Lexical""")
    nf.println(s"""import $packageName.ir._""")
    nf.println
    nf.println(s"""object Sampler {""")
    nf.println(s"""  val counter = DepthCounter""")
    nf.println(s"""  val random = new scala.util.Random""")
    nf.println(s"""  def choose[T](seq: Seq[() => T]): T = seq(random.nextInt(seq.length))()""")
    nf.println(s"""  def opt[T](valid: Boolean, elem: => T): Option[T] = if (valid && random.nextBoolean) Some(elem) else None""")
    nf.println(s"""  def IdentifierName(depth: Int): Lexical = Lexical("IdentifierName", (random.nextInt(26) + 'a').toChar.toString)""")
    nf.println(s"""  def RegularExpressionLiteral(depth: Int): Lexical = Lexical("RegularExpressionLiteral", "/x/g")""")
    nf.println(s"""  def NullLiteral(depth: Int): Lexical = Lexical("NullLiteral", "null")""")
    nf.println(s"""  def BooleanLiteral(depth: Int): Lexical = Lexical("BooleanLiteral", random.nextBoolean.toString)""")
    nf.println(s"""  def NumericLiteral(depth: Int): Lexical = Lexical("NumericLiteral", "0")""")
    nf.println(s"""  def StringLiteral(depth: Int): Lexical = Lexical("StringLiteral", "''")""")
    nf.println(s"""  def NoSubstitutionTemplate(depth: Int): Lexical = Lexical("StringLiteral", "``")""")
    nf.println(s"""  def TemplateHead(depth: Int): Lexical = Lexical("StringLiteral", "`$${")""")
    nf.println(s"""  def TemplateMiddle(depth: Int): Lexical = Lexical("StringLiteral", "}$${")""")
    nf.println(s"""  def TemplateTail(depth: Int): Lexical = Lexical("StringLiteral", "}`")""")
    targetProds.foreach(getSampler)
    nf.println(s"""}""")
    nf.close()
  }
}
