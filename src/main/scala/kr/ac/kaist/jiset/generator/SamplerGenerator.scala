package kr.ac.kaist.jiset.generator

import java.io.PrintWriter
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._

object SamplerGenerator {
  def apply(packageName: String, modelDir: String, grammar: Grammar, debug: Boolean = false): Unit = {
    val nf = getPrintWriter(s"$modelDir/Sampler.scala")
    val Grammar(lexProds, prods) = grammar

    def getSampler(prod: Production): Unit = {
      val Production(lhs, rhsList) = prod
      val Lhs(name, rawParams) = lhs
      val params = rawParams.map("p" + _)
      val paramsStr = params.map(_ + ": Boolean").mkString(", ")
      nf.println(s"""  def $name($paramsStr): $name = {""")
      nf.println(s"""    var candidates: Vector[() => $name] = Vector()""")
      rhsList.zipWithIndex.foreach {
        case (rhs, i) => getCandidates(name, params, rhs.tokens, rhs.cond, i)
      }
      nf.println(s"""    choose(candidates)""")
      nf.println(s"""  }""")
    }

    def getCandidates(name: String, bs: List[String], tokens: List[Token], cond: String, idx: Int): Unit = {
      val astName = s"$name$idx"
      val params = tokens.flatMap(tokenSampler).map(_ + ", ").mkString
      val bsstr = bs.mkString(", ")
      var sampler = s"$astName(${params}List($bsstr))"
      nf.print("    ")
      if (cond != "") nf.print(s"if ($cond) ")
      nf.println(s"""candidates :+= { () => $sampler }""")
    }

    lazy val paramMap: Map[String, List[String]] =
      prods.map(prod => prod.lhs.name -> prod.lhs.params).toMap
    def getArgs(name: String, args: List[String]): String = {
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
      s"""(${params.map(p => argMap.getOrElse(p, "false")).mkString(", ")})"""
    }
    def tokenSampler(token: Token): Option[String] = token match {
      case NonTerminal(name, args, optional) =>
        val sampler = name + getArgs(name, args)
        Some(if (optional) s"opt($sampler)" else sampler)
      case ButNot(base, _) => tokenSampler(base)
      case _ => None
    }

    nf.println(s"""package $packageName.model""")
    nf.println
    nf.println(s"""import $packageName.Lexical""")
    nf.println(s"""import $packageName.ir._""")
    nf.println
    nf.println(s"""object Sampler {""")
    nf.println(s"""  val random = new scala.util.Random""")
    nf.println(s"""  def choose[T](seq: Seq[() => T]): T = seq(random.nextInt(seq.length))()""")
    nf.println(s"""  def opt[T](elem: T): Option[T] = if (random.nextBoolean) Some(elem) else None""")
    nf.println(s"""  def IdentifierName(): Lexical = Lexical("IdentifierName", (random.nextInt(26) + 'a').toChar.toString)""")
    nf.println(s"""  def RegularExpressionLiteral(): Lexical = Lexical("RegularExpressionLiteral", "/x/g")""")
    nf.println(s"""  def NullLiteral(): Lexical = Lexical("NullLiteral", "null")""")
    nf.println(s"""  def BooleanLiteral(): Lexical = Lexical("BooleanLiteral", random.nextBoolean.toString)""")
    nf.println(s"""  def NumericLiteral(): Lexical = Lexical("NumericLiteral", "0")""")
    nf.println(s"""  def StringLiteral(): Lexical = Lexical("StringLiteral", "''")""")
    nf.println(s"""  def NoSubstitutionTemplate(): Lexical = Lexical("StringLiteral", "``")""")
    nf.println(s"""  def TemplateHead(): Lexical = Lexical("StringLiteral", "`$${")""")
    nf.println(s"""  def TemplateMiddle(): Lexical = Lexical("StringLiteral", "}$${")""")
    nf.println(s"""  def TemplateTail(): Lexical = Lexical("StringLiteral", "}`")""")
    prods.foreach(getSampler)
    nf.println(s"""}""")
    nf.close()
  }
}
