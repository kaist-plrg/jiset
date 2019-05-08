package kr.ac.kaist.ase

import scala.io.Source
import scala.util.Random
import scala.math._
import java.io.FileNotFoundException
import scala.io.StdIn
import scala.collection.mutable.HashMap
import com.codecommit.gll._

object ASE extends RegexParsers {

  val thres = 0.5
  val dep_const = 0.7

  /*
  def findFirst(stringArr: List[String]): (Int, List[(String, Double)]) = {
    val countMap: HashMap[String, Int] = HashMap()
    var x = 0
    stringArr.foreach((t) => {
      x = x + 1
      (t take 1).foreach((i) => countMap.update(i, countMap.getOrElse(i, 0) + 1))
    })
    (x, countMap.map { case (k, v) => (k, v.toDouble / x) }.toList.sortBy {
      case (k, v) => -v
    })
  }
   

  def findProb(
      tokenArr: List[List[String]],
      s: List[String]
  ): (Int, List[(String, Double)]) = {
    val countMap: HashMap[String, Int] = HashMap()
    var x = 0
    tokenArr.foreach(
      (t) =>
        if (s.forall((i) => t contains i)) {
          x = x + 1
          t.distinct.foreach(
            (i) =>
              if (!(s contains i))
                countMap.update(i, countMap.getOrElse(i, 0) + 1)
              else ()
          )
        } else {
          ()
        }
    )

    def aux(tokenArr: List[List[String]], s: List[String], f: String): Double = {
      var y = 0
      var z = 0
      tokenArr.foreach((t) =>
        if (t contains f) {
          y = y + 1
          if (s.forall((i) => t contains i)) {
            z = z + 1
          }
        })
      z.toDouble / y
    }

    (x, countMap.map { case (k, v) => (k, v.toDouble / x) }.toList.sortBy {
      case (k, v) => -v
    } take 10)
  }
  */

  def printInfo(tokenArr: List[String], g: LLGrammar): Unit = {
    // g.pprint()
    val totalL = tokenArr.length
    val validL = tokenArr.filter((i) => g contains i)
    val notL = tokenArr.filter((i) => !(g contains i)).sorted
    for (i <- notL) {
      println(i)
    }
    //println(findFirst(notL))
    println(s"${validL.length} / $totalL")
  }

  def printMenu(): Unit = {
    println("1. add rule")
    println("2. merge nonterminal")
    println("3. find suggestion")
    println("4. find (first) suggestion")
  }

  def cmdLoop(tokenArr: List[String], g: LLGrammar): Unit = {
    printInfo(tokenArr, g)
    // printMenu()
    // print(">> ")
    ()
    /*
    val f = Option(StdIn.readLine)
    val cmd = f match {
      case None => ExitCmd()
      case Some(f) => f match {
        case "1" => {
          val o = JParser.parseRuleAdd(JLexer(StdIn.readLine).right.getOrElse(List()))
          o match {
            case None => NopCmd()
            case Some((s, r)) => AddCmd(s, r)
          }
        }
        case "2" => NopCmd()
        case "3" => {
          val o = JLexer(StdIn.readLine).right.getOrElse(List())
          FindCmd(o)
        }
        case "4" => {
          FindFirstCmd()
        }
        case _ => NopCmd()
      }
    }
    cmd match {
      case ExitCmd() => ()
      case NopCmd() => cmdLoop(tokenArr, g)
      case AddCmd(s, r) => cmdLoop(tokenArr, g.addGrammar(s, r))
      case FindCmd(l) => {
        println(findProb(tokenArr, l))
        cmdLoop(tokenArr, g)
      }
      case FindFirstCmd() => {
        findFirst(tokenArr) match {
          case (x, l) => l.foreach{
            case (i, j) => println(s"$i : $j")
          }
        }
        cmdLoop(tokenArr, g)
      }
    }*/
  }

  val defaultGrammar: LLGrammar = LLGrammar(List("S"), Map(), "S")

  def main(args: Array[String]) {
    println("Loading file...")
    val filename = args(0)
    val gfilename = args(1)
    val stringArr = Source.fromFile(filename).getLines.toList
    println("Loading done.")
    val grammarSum = Source
      .fromFile(gfilename)
      .getLines
      .map((i) => JParser(i))
      .foldLeft(defaultGrammar) {
        case (g, Left(_)) => g
        case (g, Right((s, r))) => g.addGrammar(s, r)
      }
    cmdLoop(stringArr, grammarSum)
    /*
    tokenProb.foreach {
      case (k, v) =>
        println(s"$k => "); v.toList.sortBy(-_._2) foreach {
          case (k2, v2) => println(s"      $k2 : $v2")
        }
    }*/
    //val f = args.lift(0).map((i) => STRTOKEN(i)).getOrElse(START)
    //val g = args.lift(1).map((i) => i.toInt).getOrElse(1)
    //println(findProb(tokenArr, f, g))
  }
}
