package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.{ LOG_DIR, EDITOR_LOG_DIR, LINE_SEP }
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir.Logger
import kr.ac.kaist.jiset.ir.JsonProtocol._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.{ UIdGen, UId }
import scala.math.log
import io.circe._, io.circe.syntax._, io.circe.parser._

// filtering JavaScript programs using a given syntactic view
object JSFilter {
  def apply(ast: AST, view: SyntacticView): Boolean =
    ast.contains(view.ast)

  // test object
  case class Test(result: Logger.Result) {
    // term frequency(tf)
    def tfNode(nid: Int): Double = if (touchedNids.contains(nid)) 1 else 0
    def tfFunc(fid: Int): Double = if (touchedFids.contains(fid)) 1 else 0

    // name
    def name: String = result.name

    // touched
    def touched: Map[Node, Int] = result.touched
    lazy val touchedNids: Set[Int] = result.touched.keySet.map(_.uid)
    lazy val touchedFids: Set[Int] = touchedNids.map(cfg.getFuncByNid(_).uid)
  }

  object Test {
    // NOTE: spec should be loaded before
    def apply(filename: String): Test = {
      println("loading", filename)
      val result = readJson[Logger.Result](filename)
      Test(result)
    }
  }

  // TestList
  case class TestList(
    tests: Array[Test], 
    nodeMap: Array[Set[Int]],
    funcMap: Array[Set[Int]]
  ) {
    // collect all touched nids
    lazy val touchedNids = nodeMap.zipWithIndex.flatMap {
      case (tids, nid) => {
        if (tids.size > 0) Some(nid)
        else None
      }
    }.toSet

    // collect all touched algos
    lazy val touchedFids = funcMap.zipWithIndex.flatMap {
      case (tids, fid) => {
        if (tids.size > 0) Some(fid)
        else None
      }
    }.toSet

    // inverse document frequency(idf)
    val idfNode = cached[Int, Double] {
      nid => log(tests.size.toDouble / nodeMap(nid).size)
    }
    val idfFunc = cached[Int, Double] {
      fid => log(tests.size.toDouble / funcMap(fid).size)
    }

    // tf-idf score by node
    def tfidfNode(nid: Int, test: Test) = test.tfNode(nid) * idfNode(nid)

    // tf-idf score by algorithm
    def tfidfFunc(fid: Int, test: Test) = test.tfFunc(fid) * idfFunc(fid)

    // TODO reduce node -> test mapping
    def reduce(): Unit = ???

    // nodeMap set size reduce (condition: size != 0 -> size != 0)

    // sorted by tf-idf score
    // lazy val sortedNodeMap: Map[Node, List[Test]] = (for {
    //   (node, tests) <- nodeMap
    //   // sort test by tfidf score
    //   sorted = tests.sortWith {
    //     case (t0, t1) => tfidf(node, t1) < tfidf(node, t0)
    //   }
    // } yield node -> sorted).toMap

    // dump
    def dump(): Unit = {
      mkdir(EDITOR_LOG_DIR)
      dumpScore()
      dumpTouched()
    }

    val SCORE_TAKE = 5

    // dump tf-idf score
    def dumpScore(): Unit = {
      mkdir(s"$EDITOR_LOG_DIR/score")

      // dump total nodes
      dumpFile(
        touchedNids.toArray.sorted.mkString(LINE_SEP),
        s"$EDITOR_LOG_DIR/score/total_node.log"
      )

      // dump total algos
      dumpFile(
        touchedFids.map(cfg.getFunc(_).name).toArray.sorted.mkString(LINE_SEP),
        s"$EDITOR_LOG_DIR/score/total_func.log"
      )

      // dump score per test
      mkdir(s"$EDITOR_LOG_DIR/score/test")
      var (features, nodes, funcs) = 
        (Map[String, Int](), Map[Int, Int](), Map[Int, Int]())
      val nfFeatureNode = getPrintWriter(s"$EDITOR_LOG_DIR/score/feature_node.tsv")
      val nfFeatureFunc = getPrintWriter(s"$EDITOR_LOG_DIR/score/feature_func.tsv")
      tests.zipWithIndex.foreach {
        case (test, tid) =>
          println("test", test.name, tid)
          // val nf = getPrintWriter(s"$EDITOR_LOG_DIR/score/test/${tid}.json")
          val nodeScores = 
            test.touchedNids.map(nid => (nid, tfidfNode(nid, test))).toArray
          val sortedNodes = nodeScores.sortWith(_._2 > _._2).take(SCORE_TAKE)
          val feature = cfg.getFuncByNid(sortedNodes(0)._1).name
          nfFeatureNode.println(s"${test.name}\t${feature}\t${sortedNodes.map(_._1).mkString("\t")}")
          
          val funcScores =
            test.touchedFids.map(fid => (fid, tfidfFunc(fid, test))).toArray
          val sortedFuncs = funcScores.sortWith(_._2 > _._2).take(SCORE_TAKE)
          nfFeatureFunc.println(s"${test.name}\t${sortedFuncs.map(p => cfg.getFunc(p._1).name).mkString("\t")}")

          features += (feature -> (features.getOrElse(feature, 0) + 1))
          sortedNodes.foreach { case (nid, _) => nodes += (nid -> (nodes.getOrElse(nid, 0) + 1)) }
          sortedFuncs.foreach { case (fid, _) => funcs += (fid -> (funcs.getOrElse(fid, 0) + 1)) }

          // val data = (test.name, sorted)
          // nf.println(data.asJson.noSpaces)
        // nf.close()
      }
      dumpJson(features.toList.sortWith(_._2 > _._2), s"$EDITOR_LOG_DIR/score/scored_feature.log")
      dumpJson(nodes.toList.sortWith(_._2 > _._2), s"$EDITOR_LOG_DIR/score/scored_node.log")
      dumpJson(funcs.map { case (fid, cnt) => {
        cfg.getFunc(fid).name -> cnt
      }}.toList.sortWith(_._2 > _._2), s"$EDITOR_LOG_DIR/score/scored_func.log")
      nfFeatureNode.close()
      nfFeatureFunc.close()

      // // dump score per node
      // mkdir(s"$EDITOR_LOG_DIR/score/node")
      // nodeMap.zipWithIndex.foreach {
      //   case (tids, nid) if tids.size != 0 => {
      //     val node = cfg.nidGen.get(nid)
      //     val nf = getPrintWriter(s"$EDITOR_LOG_DIR/score/node/${nid}.csv")
      //     tids.toArray.foreach { tid =>
      //       val test = tests(tid)
      //       nf.println(s"${tfidf(node, test)}, ${test.name}")
      //     }
      //     nf.close()
      //     println("node", nid)
      //   }
      //   case _ =>
      // }
    }

    def dumpTouched(): Unit = {
      mkdir(s"$EDITOR_LOG_DIR/touched")

      // dump touched size of each test
      val nfTest = getPrintWriter(s"$EDITOR_LOG_DIR/touched/test.csv")
      tests.foreach { test =>
        nfTest.println(s"${test.name},${test.touched.size}")
      }
      nfTest.close()

      // dump touched size of each node
      val nfNode = getPrintWriter(s"$EDITOR_LOG_DIR/touched/node.tsv")
      nodeMap.zipWithIndex.foreach {
        case (tids, nid) => {
          val node = cfg.nidGen.get(nid)
          nfNode.println(s"${nid}\t${cfg.funcOf(node).name}\t${tids.size}")
        }
      }
      nfNode.close()
    }

    override def toString: String = ""
  }
  object TestList {
    val RESULT_DIR = s"$EDITOR_LOG_DIR/result"
    // NOTE: spec should be loaded before
    def apply(): TestList = {
      var tests: Vector[Test] = Vector()
      val nodeMap: Array[Set[Int]] = Array.fill(cfg.nodes.size)(Set())
      val funcMap: Array[Set[Int]] = Array.fill(cfg.funcs.size)(Set())
      // walk visited-nodes directory
      for {
        file <- walkTree(RESULT_DIR) if jsonFilter(file.getName)
        test = Test(file.toString) if test.touched.size > 0 // read test file
      } {
        val tid = tests.size

        // update nodeMap from test touched
        test.touchedNids.foreach(nid => nodeMap(nid) = (nodeMap(nid) + tid))

        // update funcMap from test touched
        test.touchedFids.foreach(fid => funcMap(fid) = (funcMap(fid) + tid))

        // update total test list
        tests :+= test
      }
      TestList(tests.toArray, nodeMap, funcMap)
    }
  }
}
