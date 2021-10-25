package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.{ LOG_DIR, EDITOR_LOG_DIR }
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir.Logger
import kr.ac.kaist.jiset.ir.JsonProtocol._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.{ UIdGen, UId }
import scala.math.log10

// filtering JavaScript programs using a given syntactic view
object JSFilter {
  def apply(ast: AST, view: SyntacticView): Boolean =
    ast.contains(view.ast)

  // test object
  case class Test(result: Logger.Result) {
    def name: String = result.name
    def touched: Map[Node, Int] = result.touched

    // stats
    def max: Int = ???
    lazy val sum: Int = touched.values.sum

    // term frequency(tf)
    def tf_binary(n: Node) = if (tf_raw(n) == 0) 0 else 1
    def tf_raw(n: Node) = touched.getOrElse(n, 0)
    def tf_basic(n: Node) = tf_raw(n) / sum
    def tf_log(n: Node) = log10(1 + tf_raw(n))
    def tf_augmented(n: Node, k: Double = 0.5) = k + k * tf_raw(n) / max
  }

  object Test {
    // NOTE: spec should be loaded before
    def apply(filename: String): Test = {
      val result = readJson[Logger.Result](filename)
      Test(result)
    }
  }

  // TestList
  case class TestList(tests: Array[Test], nodeMap: Array[Set[Int]]) {
    // inverse document frequency(idf)
    def n_t(n: Node) = nodeMap(n.uid).size + 1
    def idf_basic(n: Node) = log10(tests.size / n_t(n))
    def idf_smooth(n: Node) = log10(tests.size / n_t(n)) + 1
    def idf_prob(n: Node) = log10((tests.size - n_t(n)) / n_t(n))
    def tfidf(n: Node, test: Test) = test.tf_raw(n) * idf_smooth(n)

    // sorted by tf-idf score
    // lazy val sortedNodeMap: Map[Node, List[Test]] = (for {
    //   (node, tests) <- nodeMap
    //   // sort test by tfidf score
    //   sorted = tests.sortWith {
    //     case (t0, t1) => tfidf(node, t1) < tfidf(node, t0)
    //   }
    // } yield node -> sorted).toMap

    // dump
    def dump(dirname: String = EDITOR_LOG_DIR): Unit = {
      mkdir(dirname)
      dumpScore(dirname)
      dumpTestTouched(dirname)
      // dumpNodeTouched(dirname)
    }

    // dump tf-idf score
    def dumpScore(dirname: String): Unit = {
      mkdir(s"$dirname/tfidf")
      nodeMap.zipWithIndex.foreach {
        case (tids, nid) => {
          if (tids.size != 0) {
            val node = cfg.nidGen.get(nid)
            val nf = getPrintWriter(s"$dirname/tfidf/${nid}.csv")
            tids.toArray.foreach { tid =>
              val test = tests(tid)
              nf.println(s"${tfidf(node, test)}, ${test.name}")
            }
            nf.close()
            println(nid)
          }
        }
      }
    }

    // dump touched size of each test
    def dumpTestTouched(dirname: String): Unit = {
      val nf = getPrintWriter(s"$dirname/test_touched.csv")
      tests.foreach { test =>
        nf.println(s"${test.name},${test.touched.size}")
      }
      nf.close()
    }

    // dump touched test size of each node in cfg
    def dumpNodeTouched(dirname: String): Unit = {
      val nf = getPrintWriter(s"$dirname/node_touched.tsv")
      nodeMap.zipWithIndex.foreach {
        case (tids, nid) => {
          val node = cfg.nidGen.get(nid)
          nf.println(s"${nid}\t${cfg.funcOf(node).name}\t${tids.size}")
        }
      }
      nf.close()
    }

    override def toString: String = ""
  }
  object TestList {
    val RESULT_DIR = s"$EDITOR_LOG_DIR/result"
    // NOTE: spec should be loaded before
    def apply(): TestList = {
      var tests: Vector[Test] = Vector()
      val nodeMap: Array[Set[Int]] = Array.fill(cfg.nodes.size)(Set())
      // walk visited-nodes directory
      for {
        file <- walkTree(RESULT_DIR) if jsonFilter(file.getName)
        test = Test(file.toString) // read test file
      } {
        val tid = tests.size

        // update nodeMap from test trace
        test.touched.foreach {
          case (node, cnt) =>
            if (cnt > 0) {
              val nid = node.uid
              nodeMap(nid) = (nodeMap(nid) + tid)
            }
        }

        // update total test list
        tests :+= test

        println(tid, test.name)
      }
      TestList(tests.toArray, nodeMap)
    }
  }
}
