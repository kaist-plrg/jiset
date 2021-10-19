package kr.ac.kaist.jiset.viewer

import kr.ac.kaist.jiset.{ VISITED_LOG_DIR, BASE_DIR }
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.util.JvmUseful._
import scala.math.log10

// filtering JavaScript programs using a given syntactic view
object JSFilter {
  def apply(ast: AST, view: SyntacticView): Boolean =
    ast.contains(view.ast)

  // test with trace
  case class Test(name: String, trace: Map[Node, Int]) {
    def max: Int = ???
    def sum: Int = trace.values.sum

    // term frequency(tf)
    def tf_binary(node: Node) = if (trace.contains(node)) 1 else 0
    def tf_raw(node: Node) = trace.getOrElse(node, 0)
    def tf_basic(node: Node) = tf_raw(node) / sum
    def tf_log(node: Node) = log10(1 + tf_raw(node))
    def tf_augmented(node: Node, k: Double = 0.5) = k + k * tf_raw(node) / max
  }

  object Test {
    // NOTE: spec should be loaded before
    def apply(path: String): Test = {
      import cfg.jsonProtocol._
      val (name, rawTrace) =
        readJson[(String, Map[Node, (Function, Int)])](path)
      val trace = rawTrace.map { case (n, (_, c)) => n -> c }.toMap
      Test(name, trace)
    }
  }

  // TestList
  case class TestList(tests: List[Test], nodeMap: Map[Node, List[Test]]) {
    // inverse document frequency(idf)
    def n_t(node: Node) = tests.filter(_.trace.contains(node)).size + 1
    def idf_basic(node: Node) = log10(tests.size / n_t(node))
    def idf_smooth(node: Node) = log10(tests.size / (1 + n_t(node))) + 1
    def idf_prob(node: Node) = log10((tests.size - n_t(node)) / n_t(node))
    def tfidf(node: Node, test: Test) = test.tf_raw(node) * idf_smooth(node)

    // sorted by tf-idf score
    lazy val sortedNodeMap: Map[Node, List[Test]] = (for {
      (node, tests) <- nodeMap
      // sort test by tfidf score
      sorted = tests.sortWith {
        case (t0, t1) => tfidf(node, t1) < tfidf(node, t0)
      }
    } yield node -> sorted).toMap

    // dump tf-idf score
    def dumpScore(dirname: String = BASE_DIR): Unit = {
      mkdir(dirname)
      val nf = getPrintWriter(s"$dirname/tfidf.csv")
      println("!!! dump start")
      nf.println(s",${nodeMap.map(_._1.uid).mkString(",")}")
      tests.foreach { test =>
        val scores = nodeMap.map {
          case (node, touched) =>
            if (touched contains test) tfidf(node, test)
            else .0
        }
        nf.println(s"${test.name},${scores.mkString(",")}")
        println(test.name)
      }
      nf.close()
    }
  }
  object TestList {
    def apply(dirname: String = VISITED_LOG_DIR): TestList = {
      var tests: List[Test] = List()
      var nodeMap: Map[Node, List[Test]] = Map()
      // walk visited-nodes directory
      for {
        file <- walkTree(dirname) if jsonFilter(file.getName)
        test = Test(file.toString) // read test file
      } {
        // update nodeMap from test trace
        test.trace.keySet.foreach { node =>
          val testList = nodeMap.getOrElse(node, List())
          nodeMap += node -> (test :: testList)
        }
        // update total test list
        tests ::= test
        println(test.name)
      }
      println("!!! initialized")
      val tl = TestList(tests, nodeMap) // TODO sort tests
      println("!!! create test list obj")
      tl.dumpScore()
      tl
    }
  }
}
