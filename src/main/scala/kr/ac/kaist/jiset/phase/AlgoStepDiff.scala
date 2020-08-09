package kr.ac.kaist.jiset.phase

import java.io.File
import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.{ DIFFLIST, RESOURCE_DIR }
import kr.ac.kaist.jiset.algorithm._
import kr.ac.kaist.jiset.model.AlgoCompiler
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.{ LINE_SEP, JISETConfig }
import scala.Console.{ RESET, GREEN }
import scala.collection.mutable.{ Map => MMap }
import scala.io.Source

// AlgoStepDiff phase
case object AlgoStepDiff extends PhaseObj[Unit, AlgoStepDiffConfig, Unit] {
  val name = "algo-step-diff"
  val help = "Show differences among ECMAScript algorithm steps of adjacent versions."

  def apply(
    unit: Unit,
    jisetConfig: JISETConfig,
    config: AlgoStepDiffConfig
  ): Unit = {
    apply("general", GeneralAlgoCompiler("", _))
    apply("manual", AlgoCompiler("", _))
  }

  def apply(
    genName: String,
    algoGen: Algorithm => GeneralAlgoCompilerHelper
  ): Unit = DIFFLIST match {
    case base :: rest =>
      println("calculating algorithm steps...")
      val initSt = getState(base, algoGen)
      val initRes = getRes(initSt)
      val (_, res) = rest.foldLeft((initSt, initRes)) {
        case ((prevSt, prevRes), _version) =>
          val State(prevVersion, prevAlgoMap, _, _) = prevSt
          val AllResult(each, diff) = prevRes
          val st = getState(_version, algoGen)
          val version = _version.dropRight(5)
          val tlb = getTLB(st)
          val diffLang = MMap[String, Boolean]()
          val diffBuiltin = MMap[String, Boolean]()
          for ((name, (algo, failed, _)) <- st.algoMap) {
            val diffStepMap = if (algo.lang) diffLang else diffBuiltin
            val diffStepSet = prevAlgoMap
              .get(name)
              .map { case (_, _, x) => findDiffStepSet(x, splitAlgo(algo)) }
              .getOrElse((0 until algo.lineCount).toList)
            (0 until algo.lineCount).foreach((k) => {
              if (diffStepSet contains k)
                diffStepMap += s"$name$k" -> !(failed contains k)
            })
          }
          val diffVersion = s"$prevVersion-$version"
          val langRes @ Result(langSucc, langTotal) = countPass(diffLang.toMap)
          val builtinRes @ Result(builtinSucc, builtinTotal) = countPass(diffBuiltin.toMap)
          val diffTLB = TLB(
            Result(langSucc + builtinSucc, langTotal + builtinTotal),
            langRes,
            builtinRes
          )
          (st, AllResult(each + (version -> tlb), diff + (diffVersion -> diffTLB)))
      }
      val (each, diff) = getAverage(res)
      println("========================================")
      println(s"ECMAScript each version ($genName)")
      println("========================================")
      for ((version, TLB(all, lang, builtin)) <- res.each) {
        println(f"${"   "}%-8s Total   : ${getCountString(all)}")
        println(f"$version%-8s Lang    : ${getCountString(lang)}")
        println(f"${"   "}%-8s Builtin : ${getCountString(builtin)}")
        println("----------------------------------------")
      }
      println(f"${"    "}%-8s Total   : ${getCountString(each.all)}")
      println(f"${"avg."}%-8s Lang    : ${getCountString(each.lang)}")
      println(f"${"    "}%-8s Builtin : ${getCountString(each.builtin)}")
      println("========================================")
      println
      println("========================================")
      println(s"ECMAScript update ($genName)")
      println("========================================")
      for ((version, TLB(all, lang, builtin)) <- res.diff) {
        println(f"${"   "}%-15s Total   : ${getCountString(all)}")
        println(f"$version%-15s Lang    : ${getCountString(lang)}")
        println(f"${"   "}%-15s Builtin : ${getCountString(builtin)}")
        println("----------------------------------------")
      }
      println(f"${"    "}%-15s Total   : ${getCountString(diff.all)}")
      println(f"${"avg."}%-15s Lang    : ${getCountString(diff.lang)}")
      println(f"${"    "}%-15s Builtin : ${getCountString(diff.builtin)}")
      println("========================================")
    case Nil =>
  }

  case class Result(succ: Int, total: Int)
  case class TLB(all: Result, lang: Result, builtin: Result)
  case class AllResult(each: Map[String, TLB], diff: Map[String, TLB])
  type AlgoMap = Map[String, (Algorithm, Set[Int], List[List[Token]])]
  type StepMap = Map[String, Boolean]
  case class State(
      version: String,
      algoMap: AlgoMap,
      lang: StepMap,
      builtin: StepMap
  )

  def getAverage(map: Map[String, TLB]): TLB = {
    val zero = Result(0, 0)
    map.foldLeft(TLB(zero, zero, zero)) {
      case (prev, (_, tlb)) => TLB(
        Result(prev.all.succ + tlb.all.succ, prev.all.total + tlb.all.total),
        Result(prev.lang.succ + tlb.lang.succ, prev.lang.total + tlb.lang.total),
        Result(prev.builtin.succ + tlb.builtin.succ, prev.builtin.total + tlb.builtin.total)
      )
    }
  }

  def getAverage(res: AllResult): (TLB, TLB) =
    (getAverage(res.each), getAverage(res.diff))

  def getState(version: String, algoGen: Algorithm => GeneralAlgoCompilerHelper): State = {
    val dirName = s"$RESOURCE_DIR/$version/auto/algorithm"
    val lang = MMap[String, Boolean]()
    val builtin = MMap[String, Boolean]()
    val algoMap = MMap[String, (Algorithm, Set[Int], List[List[Token]])]()
    for (file <- walkTree(new File(dirName)) if jsonFilter(file.getName)) {
      val name = file.toString
      val algo = Algorithm(name)
      val stepMap = if (algo.lang) lang else builtin
      val lineCount = algo.lineCount
      lazy val compiler = algoGen(algo)
      lazy val (func, failed) = compiler.result

      algoMap += algo.filename.split("/").last -> (algo, failed.keySet, splitAlgo(algo))
      (0 until lineCount).foreach((k) => {
        stepMap += s"$name$k" -> !(failed contains k)
      })
    }
    val shortVersion = version.dropRight(5)
    val algoRes = getCountString(countPass(algoMap.toMap.map { case (k, (_, f, _)) => k -> f.isEmpty }))
    println(s"$shortVersion: $algoRes")
    State(shortVersion, algoMap.toMap, lang.toMap, builtin.toMap)
  }

  def getTLB(st: State): TLB = {
    val State(version, _, lang, builtin) = st
    val langRes @ Result(langSucc, langTotal) = countPass(lang)
    val builtinRes @ Result(builtinSucc, builtinTotal) = countPass(builtin)
    TLB(Result(langSucc + builtinSucc, langTotal + builtinTotal), langRes, builtinRes)
  }
  def getRes(st: State): AllResult = {
    AllResult(Map(st.version -> getTLB(st)), Map())
  }
  def countPass[A](k: Map[A, Boolean]): Result = Result(k.filter(_._2).size, k.size)
  def getCountString(res: Result): String = {
    val Result(pass, total) = res
    val rate = pass.toDouble / total.toDouble * 100
    f"$pass / $total $GREEN($rate%2.2f%%)$RESET"
  }

  def splitAlgo(algo: Algorithm): List[List[Token]] = (for {
    step <- algo.getSteps(Nil)
  } yield step.tokens.foldLeft(List[Token]()) {
    case (l, StepList(_)) => Out :: In :: l
    case (l, x) => x :: l
  }.reverse)

  case class Memoized[A1, A2, B](f: (A1, A2) => B) extends ((A1, A2) => B) {
    val cache = MMap.empty[(A1, A2), B]
    def apply(x: A1, y: A2) = cache.getOrElseUpdate((x, y), f(x, y))
  }

  lazy val lcsM: Memoized[List[List[Token]], List[List[Token]], List[List[Token]]] = Memoized {
    case (_, Nil) => Nil
    case (Nil, _) => Nil
    case (x :: xs, y :: ys) if x == y => x :: lcsM(xs, ys)
    case (x :: xs, y :: ys) => {
      (lcsM(x :: xs, ys), lcsM(xs, y :: ys)) match {
        case (xs, ys) if xs.length > ys.length => xs
        case (xs, ys) => ys
      }
    }
  }

  def findDiffStepSet(a: List[List[Token]], b: List[List[Token]]): List[Int] = {
    val commonSeq = lcsM(a, b)
    def aux(comm: List[List[Token]], b: List[List[Token]], i: Int, li: List[Int]): List[Int] = (comm, b, i, li) match {
      case (_, Nil, _, li) => li
      case (Nil, x :: rest, i, li) => aux(Nil, rest, i + 1, li :+ i)
      case (cx :: crest, x :: rest, i, li) => {
        val newCx = if (cx == x) crest else cx :: crest
        val newLi = if (cx == x) li else (li :+ i)
        aux(newCx, rest, i + 1, newLi)
      }
    }
    aux(commonSeq, b, 0, Nil)
  }

  def defaultConfig: AlgoStepDiffConfig = AlgoStepDiffConfig()
  val options: List[PhaseOption[AlgoStepDiffConfig]] = List()
}

// AlgoStepDiff phase config
case class AlgoStepDiffConfig() extends Config
