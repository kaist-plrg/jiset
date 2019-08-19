package kr.ac.kaist.ase.algorithm

import java.io.File
import kr.ac.kaist.ase.model.{ AlgoCompilerHelper, AlgoCompiler }
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.{ LINE_SEP, RESOURCE_DIR, VERSION }
import org.jline.builtins.Completers.TreeCompleter
import org.jline.reader._
import org.jline.reader.impl._
import org.jline.terminal._
import org.jline.utils.InfoCmp.Capability
import org.jline.utils._
import scala.Console.{ RESET, CYAN }
import scala.util.Random.shuffle
import scala.util.{ Try, Success, Failure }

import TreeCompleter._

sealed trait AbstractToken
case class AText(s: String) extends AbstractToken
case class ATag(t: String) extends AbstractToken

// REPL
object REPL extends AlgoCompilerHelper {
  val algo: Algorithm = Algorithm(0, Nil, Method, Nil, "")
  val algoName: String = ""
  val kind: AlgoKind = Method

  // algorithm files
  val algoDir = s"$RESOURCE_DIR/$VERSION/auto/algorithm"

  def run(onlyFailed: Boolean): Unit = {
    val builder: TerminalBuilder = TerminalBuilder.builder()
    val terminal: Terminal = builder.build()
    val completer: TreeCompleter = new TreeCompleter(
      node("all"),
      node("get-first"),
      node("filter", node("pre"), node("sub"), node("parser"))
    )
    val reader: LineReader = LineReaderBuilder.builder()
      .terminal(terminal)
      .completer(completer)
      .build()
    val writer = terminal.writer()

    def stopMessage(msg: String): Unit = {
      print(msg)
      System.console().reader().read
    }

    implicit def tokenListOrdering[Token]: Ordering[List[Token]] = new Ordering[List[Token]] {
      implicit def compare(x: List[Token], y: List[Token]): Int = (x, y) match {
        case (Nil, Nil) => 0
        case (Nil, _) => -1
        case (_, Nil) => 1
        case (xh :: xt, yh :: yt) =>
          val xstr = xh.toString
          val ystr = yh.toString
          if (xstr == ystr) compare(xt, yt)
          else xstr.compare(ystr)
      }
    }

    val algos: List[Algorithm] = for {
      file <- shuffle(walkTree(new File(algoDir))).toList
      filename = file.getName
      if jsonFilter(filename)
    } yield Algorithm(file.toString)

    lazy val allLists: List[List[Token]] = (for {
      algo <- algos
      step <- algo.getSteps(Nil)
    } yield (List[Token]() /: step.tokens) {
      case (l, StepList(_)) => Out :: In :: l
      case (l, x) => x :: l
    }.reverse).sorted

    def toAbstractToken(x: Token): AbstractToken = x match {
      case Text(s) => AText(s)
      case Const(_) => ATag("const")
      case Code(_) => ATag("code")
      case Value(_) => ATag("value")
      case Id(_) => ATag("id")
      case Star(_) => ATag("star")
      case Nt(_) => ATag("nt")
      case Sup(_) => ATag("sub")
      case Url(_) => ATag("url")
      case Grammar(_, _) => ATag("grammar")
      case StepList(_) => ATag("step-list")
      case Next(_) => ATag("next")
      case In => ATag("in")
      case Out => ATag("out")
    }

    lazy val twoLists: List[List[AbstractToken]] = (for {
      algo <- algos
      step <- algo.getSteps(Nil).sliding(2).map(_.foldLeft(List[AbstractToken]()) {
        case (a, b) => a ++ b.tokens.map((x) => toAbstractToken(x))
      }).toList
    } yield (List[AbstractToken]() /: step) {
      case (l, x) => x :: l
    }.reverse).sorted

    lazy val failedLists: List[List[Token]] = (for {
      algo <- algos
      (_, failed) = AlgoCompiler("", algo).result
      tokens <- failed.values
    } yield tokens).sorted

    lazy val tokenLists: List[List[Token]] = if (onlyFailed) failedLists else allLists
    lazy val total = tokenLists.length

    def prompt: String = CYAN + "repl-algo> " + RESET

    def show(list: List[List[Token]], filter: Filter = ts => true): Unit = {
      var count = 0
      list.foreach(ts => if (filter(ts)) {
        println(ts.mkString(" "))
        count += 1
      })
      printlnGreen(s"total: $count")
    }

    def getNext(x: List[AbstractToken]): List[List[AbstractToken]] = {
      val passRest = twoLists.map((y) => restSubSeq(x, y)).filter(!_.isEmpty).map(_.distinct)
      val passLength = passRest.length
      val passflatten = passRest.foldLeft(List[AbstractToken]()) {
        case (a, b) => a ++ b
      }
      val nm = (Map[AbstractToken, Int]() /: passflatten) {
        case (m, t) => m.get(t) match {
          case Some(k) => m + (t -> (k + 1))
          case None => m + (t -> 1)
        }
      }
      nm.toSeq.sortBy(_._2) foreach {
        case (k, v) => println(s"$k -> $v")
      }
      nm.filter { case (k, v) => v.toDouble / passLength >= 0.5 }.keys.toList.map(x :+ _)
    }

    def isSubSeq(short: List[AbstractToken], long: List[AbstractToken]): Boolean = short match {
      case Nil => true
      case a :: rest => {
        val longrest = long.dropWhile((x) => a != x)
        if (longrest.isEmpty) false else isSubSeq(rest, longrest)
      }
    }

    def restSubSeq(short: List[AbstractToken], long: List[AbstractToken]): List[AbstractToken] = short match {
      case Nil => long
      case a :: rest => {
        val longrest = long.dropWhile((x) => a != x).drop(1)
        if (longrest.isEmpty) Nil else restSubSeq(rest, longrest)
      }
    }

    printlnGreen("Loading token lists...")
    printlnGreen(s"$total token lists loaded.")

    var keep: Boolean = true
    while (keep) try {
      reader.readLine(prompt) match {
        case null =>
        case str => str.split("\\s+").toList match {
          case Nil =>
          case cmd :: args => cmd match {
            // show all steps
            case "all" => show(tokenLists)

            // print statistics of first tokens
            case "get-first" => (Map[Token, Int]() /: tokenLists.map(_.head)) {
              case (m, t) => m.get(t) match {
                case Some(k) => m + (t -> (k + 1))
                case None => m + (t -> 1)
              }
            }.toSeq.sortBy(_._2).reverse foreach {
              case (t, k) => println(s"$t -> $k")
            }

            case "get-second" => {
              val firstList = List(List(AText("If")))
              //tokenLists.map((x) => toAbstractToken(x.head)).distinct.map(_ :: Nil)
              val nextList = firstList.foldLeft(List[List[AbstractToken]]()) {
                case (a, b) => a ++ getNext(b)
              }
              nextList foreach {
                case x => println(s"$x")
              }
            }

            // filtering
            case "filter" => args match {
              // no filter
              case Nil => printlnRed(s"no filter for `filter` command")
              case fname :: args => ((fname match {
                // prefix filter
                case "pre" => Left(preFilter)
                // sub-sequence filter
                case "sub" => Left(subFilter)
                // parser filter
                case "parser" => Left(parserFilter)
                // unparser filter
                case "unparser" => Left(unparserFilter)
                // unknown filter
                case f => Right(f)
              }): Either[GenFilter, String]) match {
                case Left(filter) =>
                  show(tokenLists, filter(args))
                case Right(f) =>
                  printlnRed(s"unknown filter for `filter` command: $f")
              }
            }

            // exit repl
            case "exit" => keep = false

            // unknown commands
            case t => printlnRed(s"unknown commands: $t")
          }
        }
      }
    } catch {
      case e: EndOfFileException => keep = false
      case e: UserInterruptException => keep = false
      case e: java.lang.RuntimeException => { println(e.getMessage); stopMessage(s"") }
      case e: Throwable => stopMessage(s"ERROR: $e")
    }
  }

  // filters
  type Filter = List[Token] => Boolean
  type GenFilter = List[String] => Filter

  // prefix filter generator
  lazy val preFilter: GenFilter = ss => ts => (ts, ss) match {
    case (_, Nil) => true
    case (Nil, _) => false
    case (t :: ttl, ss @ s :: stl) =>
      if (t is s) preFilter(stl)(ttl)
      else false
  }

  // sub-sequence filter generator
  lazy val subFilter: GenFilter = ss => ts => (ts, ss) match {
    case (_, Nil) => true
    case (Nil, _) => false
    case (t :: ttl, ss @ s :: stl) =>
      if (t is s) subFilter(stl)(ttl)
      else subFilter(ss)(ttl)
  }

  // parser filter generator
  lazy val parserFilter: GenFilter = ss => ts => {
    val p = getParser(ss)
    parseAll(p, ts).successful
  }

  // unparser filter generator
  lazy val unparserFilter: GenFilter = ss => ts => {
    val p = getParser(ss)
    !parseAll(p, ts).successful
  }

  def getParser(ss: List[String]): Parser[Unit] = {
    // TODO
    stmt ^^^ { () }
  }
}
