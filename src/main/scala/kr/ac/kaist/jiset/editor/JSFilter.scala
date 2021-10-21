package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.BASE_DIR
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

  // test id generator
  val tidGen: UIdGen[Test] = new UIdGen

  // test object
  case class Test(
    uidGen: UIdGen[Test],
    result: Logger.Result
  ) extends UId[Test] {
    def name: String = result.name
    def touched: Map[Node, Int] = result.touched

    // stats
    def max: Int = ???
    def sum: Int = touched.values.sum

    // term frequency(tf)
    def tf_binary(n: Node) = if (touched.contains(n)) 1 else 0
    def tf_raw(n: Node) = touched.getOrElse(n, 0)
    def tf_basic(n: Node) = tf_raw(n) / sum
    def tf_log(n: Node) = log10(1 + tf_raw(n))
    def tf_augmented(n: Node, k: Double = 0.5) = k + k * tf_raw(n) / max
  }

  object Test {
    // NOTE: spec should be loaded before
    def apply(filename: String): Test = {
      val result = readJson[Logger.Result](filename)
      Test(tidGen, result)
    }
  }

  // TestList
  case class TestList(tests: List[Test], nodeMap: Map[Node, List[Test]]) {
    // inverse document frequency(idf)
    def n_t(n: Node) = nodeMap.getOrElse(n, List()).size + 1
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
    def dump(dirname: String = BASE_DIR): Unit = {
      mkdir(dirname)
      dumpScore(dirname)
      dumpTestTouched(dirname)
      dumpNodeTouched(dirname)
    }

    // dump tf-idf score
    def dumpScore(dirname: String = BASE_DIR): Unit = {
      val nf = getPrintWriter(s"$dirname/tfidf.csv")
      nf.println(s",${nodeMap.map(_._1.uid).mkString(",")}")
      tests.foreach { test =>
        val scores = nodeMap.map {
          case (node, touched) =>
            if (touched contains test) tfidf(node, test)
            else .0
        }
        nf.println(s"${test.name},${scores.mkString(",")}")
      }
      nf.close()
    }

    // dump touched size of each test
    def dumpTestTouched(dirname: String = BASE_DIR): Unit = {
      val nf = getPrintWriter(s"$dirname/test_touched.csv")
      tests.foreach { test =>
        nf.println(s"${test.name},${test.touched.size}")
      }
      nf.close()
    }

    // dump touched test size of each node in cfg
    def dumpNodeTouched(dirname: String = BASE_DIR): Unit = {
      val nf = getPrintWriter(s"$dirname/node_touched.tsv")
      cfg.nodes.sortWith(_.uid < _.uid).foreach { node =>
        {
          val names = nodeMap.getOrElse(node, List()).map(_.name)
          nf.println(s"${node.uid}\t${cfg.funcOf(node).name}\t${names.size}")
        }
      }
      nf.close()
    }

    override def toString: String = ???
  }
  object TestList {
    // NOTE: spec should be loaded before
    // TODO
    def apply(dirname: String = ???): Unit = {
      var tests: List[Test] = List()
      var nodeMap: Map[Node, List[Test]] = Map()
      // walk visited-nodes directory
      for {
        file <- walkTree(dirname) if jsonFilter(file.getName)
        test = Test(file.toString) // read test file
      } {
        // update nodeMap from test trace
        test.touched.keySet.foreach { node =>
          val testList = nodeMap.getOrElse(node, List())
          nodeMap += node -> (test :: testList)
        }
        // update total test list
        tests ::= test
      }
      TestList(tests, nodeMap)
    }
  }
}
