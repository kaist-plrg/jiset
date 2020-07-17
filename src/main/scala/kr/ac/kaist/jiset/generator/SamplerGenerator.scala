package kr.ac.kaist.jiset.generator

import java.io.PrintWriter
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._

object SamplerGenerator {
  def apply(packageName: String, modelDir: String, grammar: Grammar, debug: Boolean = false): Unit = {
    dumpSampler(packageName, modelDir, grammar, debug)
    dumpNRSampler(packageName, modelDir, grammar, debug)
  }

  def dumpSampler(packageName: String, modelDir: String, grammar: Grammar, debug: Boolean): Unit = {
    val nf = getPrintWriter(s"$modelDir/Sampler.scala")
    val Grammar(lexProds, prods) = grammar
    val targetProds = prods.filter(!_.lhs.isModule)

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
      nf.println(s"""    choose(candidates)()""")
      nf.println(s"""  }""")
    }

    def getCandidates(name: String, bs: List[String], rhs: Rhs, idx: Int): Unit = {
      if (!rhs.containsModuleNT) {
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
    nf.println(s"""import $packageName.util.Useful._""")
    nf.println
    nf.println(s"""class Sampler {""")
    nf.println(s"""  val counter = DepthCounter""")
    nf.println(s"""  val random = new scala.util.Random""")
    nf.println(s"""  def opt[T](valid: Boolean, elem: => T): Option[T] = if (valid && random.nextBoolean) Some(elem) else None""")
    nf.println(s"""  def IdentifierName(depth: Int): Lexical = Lexical("IdentifierName", (random.nextInt(26) + 'a').toChar.toString)""")
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

  def dumpNRSampler(packageName: String, modelDir: String, grammar: Grammar, debug: Boolean): Unit = {
    val nf = getPrintWriter(s"$modelDir/NonRecursiveSampler.scala")
    val Grammar(lexProds, prods) = grammar
    val targetProds = prods.filter(!_.lhs.isModule)

    def getSampler(prod: Production): Unit = {
      val Production(lhs, rhsList) = prod
      val Lhs(name, rawParams) = lhs
      val params = rawParams.map("p" + _)
      val paramsStr = params.map(_ + ": Boolean").mkString(", ")
      val argStr = params.mkString(", ")

      // all
      nf.println(s"""  def $name($paramsStr): ScalaSet[$name] = {""")
      nf.println(s"""    if (!visited.contains("$name", List($argStr))) {""")
      nf.println(s"""      visited += (("$name", List($argStr)))""")
      nf.println(s"""      var s = ScalaSet[$name]()""")
      rhsList.zipWithIndex.foreach {
        case (rhs, i) => getCandidates(name, params, rhs, i)
      }
      nf.println(s"""      s""")
      nf.println(s"""    } else {""")
      nf.println(s"""      val d = counter.${name}($argStr).depth""")
      nf.println(s"""      val sample = randomSampler.$name(d${params.map(s => s", $s").mkString("")})""")
      nf.println(s"""      ScalaSet(sample)""")
      nf.println(s"""    }""")
      nf.println(s"""  }""")
    }

    def getCandidates(name: String, bs: List[String], rhs: Rhs, idx: Int): Unit = {
      // rhs
      // val listItems = StatementListItem(pYield, pAwait, pReturn)
      // s = s union listItems.map(StatementList0(_, List(pYield, pAwait, pReturn)))
      if (!rhs.containsModuleNT) {
        val Rhs(tokens, cond) = rhs
        val space = if (cond != "") "        " else "      "
        if (cond != "") nf.println(s"      if ($cond) {")
        // val listItems = StatementListItem(pYield, pAwait, pReturn), tokenIdx
        val tokenInfo = tokens.zipWithIndex.flatMap {
          case (token, i) => tokenSampler(token) match {
            case Some((name, argsStr, optional)) =>
              val id = s"${name}_${idx}_${i}"
              nf.println(space + s"val $id = $name($argsStr).toList")
              Some((id, s"$name($argsStr).head", optional))
            case None => None
          }
        }
        val (tokenIds, tokenDefaults, tokenOptional) = tokenInfo.unzip3
        if (tokenIds.isEmpty)
          nf.println(space + s"s += $name$idx(List(${bs.mkString(", ")}))")
        else {
          nf.println(space + s"val max${idx} = List(${tokenIds.mkString(", ")}).map(_.length).max")
          nf.println(space + s"for (i <- (0 until max${idx})) {")
          tokenIds.zipWithIndex.foreach {
            case (tid, i) => {
              nf.println(space + s"  val arg$i = $tid.applyOrElse(i, (_: Int) => ${tokenDefaults(i)})")
            }
          }

          var argsList = tokenOptional.zipWithIndex.foldLeft(List(List[String]())) {
            case (argsList, (optional, i)) => {
              val argStr = s"arg$i"
              if (optional) argsList.flatMap(l => List(l :+ s"Some($argStr)", l :+ "None"))
              else argsList.map(_ :+ argStr)
            }
          }

          argsList.foreach((args: List[String]) => {
            val argStr = args.mkString(", ")
            nf.println(space + s"  s += $name$idx($argStr, List(${bs.mkString(", ")}))")
          })
          nf.println(space + s"}")
        }

        if (cond != "") nf.println(s"      }")
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
    def tokenSampler(token: Token): Option[(String, String, Boolean)] = token match {
      case NonTerminal(name, params, optional) => {
        val args = getArgs(name, params)
        val argsStr = args.mkString(", ")
        Some((name, argsStr, optional))
      }
      case ButNot(base, _) => tokenSampler(base)
      case _ => None
    }

    nf.println(s"""package $packageName.model""")
    nf.println
    nf.println(s"""import $packageName.Lexical""")
    nf.println(s"""import $packageName.ir._""")
    nf.println(s"""import $packageName.util.Useful._""")
    nf.println(s"""import scala.collection.immutable.{ Set => ScalaSet }""")
    nf.println
    nf.println(s"""class NonRecursiveSampler {""")
    nf.println(s"""  val counter = DepthCounter""")
    nf.println(s"""  val randomSampler = new Sampler""")
    nf.println(s"""  var visited: ScalaSet[(String, List[Boolean])] = ScalaSet()""")
    nf.println
    nf.println(s"""  private def IdentifierName(): ScalaSet[Lexical] = ScalaSet(randomSampler.IdentifierName(1))""")
    nf.println(s"""  private def NullLiteral(): ScalaSet[Lexical] = ScalaSet(randomSampler.NullLiteral(1))""")
    nf.println(s"""  private def BooleanLiteral(): ScalaSet[Lexical] = ScalaSet(randomSampler.BooleanLiteral(1))""")
    nf.println(s"""  private def NumericLiteral(): ScalaSet[Lexical] = ScalaSet(randomSampler.NumericLiteral(1))""")
    nf.println(s"""  private def StringLiteral(): ScalaSet[Lexical] = ScalaSet(randomSampler.StringLiteral(1))""")
    nf.println(s"""  private def NoSubstitutionTemplate(): ScalaSet[Lexical] = ScalaSet(randomSampler.NoSubstitutionTemplate(1))""")
    nf.println(s"""  private def TemplateHead(): ScalaSet[Lexical] = ScalaSet(randomSampler.TemplateHead(1))""")
    nf.println(s"""  private def TemplateMiddle(): ScalaSet[Lexical] = ScalaSet(randomSampler.TemplateMiddle(1))""")
    nf.println(s"""  private def TemplateTail(): ScalaSet[Lexical] = ScalaSet(randomSampler.TemplateTail(1))""")
    targetProds.foreach(getSampler)
    nf.println(s"""}""")
    nf.close()
  }
}
