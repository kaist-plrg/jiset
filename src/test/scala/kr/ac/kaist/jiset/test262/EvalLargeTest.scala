package kr.ac.kaist.jiset.test262

import java.text.SimpleDateFormat
import java.util.Date
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Useful._

class EvalLargeTest extends Test262Test {
  val name: String = "test262EvalTest"

  // sealed trait TestKind
  // case object Basic extends TestKind
  // case object Long extends TestKind
  // case object VeryLong extends TestKind
  // case object Manual extends TestKind

  // print results after all tests
  override def afterAll(): Unit = {
    val suffix = new SimpleDateFormat("yyMMddHHmm").format(new Date())
    val filename = s"$TEST_DIR/result/${tag}_${suffix}"

    val nf = getPrintWriter(filename)
    resMap.toList.sortBy { case (k, v) => k }.foreach {
      case (k, v) => nf.println(s"$k: $v")
    }
    nf.close()
  }

  // tests for js evaluation
  def evalJSTest(st: State): Unit = {
    st.context.locals.get(st.context.retId) match {
      case Some(addr: Addr) => st.heap(addr, Str("Type")) match {
        case (addr: Addr) =>
          assert(addr == st.globals.getOrElse(Id("CONST_normal"), Absent))
          st.heap(NamedAddr("REALM"), Str("printStr")) match {
            case Str(v) if (v contains "AsyncTestFailure") =>
              fail(s"print test failure: $v")
            case _ => ()
          }
        case v => fail(s"invalid completion type: $v")
      }
      case Some(v) => fail(s"return not an address: $v")
      case None => fail("no return value")
    }
  }

  // TODO registration
  // val dir = new File(test262Dir)
  // val (config, evalConfig) = testKind match {
  //   case Basic => (FilterMeta.test262configSummary, new IREvalConfig(timeout = Some(10)))
  //   case Long => (FilterMeta.test262LongconfigSummary, new IREvalConfig(timeout = None))
  //   case VeryLong => (FilterMeta.test262VeryLongconfigSummary, new IREvalConfig(timeout = None))
  //   case Manual => (FilterMeta.test262ManualconfigSummary, new IREvalConfig(timeout = Some(10)))
  // }
  // val initInclude = List("assert.js", "sta.js").foldLeft(Map[String, Either[String, List[StatementListItem]]]()) {
  //   case (imm, s) => {
  //     val includeName = s"${dir.toString}/harness/$s"
  //     val jsConfig = aseConfig.copy(fileNames = List(includeName))
  //     val stmtList = ModelHelper.flattenStatement(Parse((), jsConfig))
  //     imm + (s -> Right(stmtList))
  //   }

  // }
  // val includeMap: Map[String, Either[String, List[StatementListItem]]] = config.normal.foldLeft(initInclude) {
  //   case (im, NormalTestConfig(_, includes)) =>
  //     includes.foldLeft(im) {
  //       case (imm, s) => if (imm contains s) {
  //         imm
  //       } else {
  //         val includeName = s"${dir.toString}/harness/$s"
  //         val jsConfig = aseConfig.copy(fileNames = List(includeName))
  //         val ast = Parse((), jsConfig)
  //         val eStmtList = try {
  //           ModelHelper.checkSupported(ast)
  //           val stmtList = ModelHelper.flattenStatement(ast)
  //           Right(stmtList)
  //         } catch {
  //           case NotSupported(msg) => Left(msg)
  //         }
  //         imm + (s -> eStmtList)
  //       }
  //     }
  // }

  def init: Unit = {
    // TODO
    // val initStList = for {
    //   x <- includeMap("assert.js")
    //   y <- includeMap("sta.js")
    // } yield x ++ y
    // for (NormalTestConfig(filename, includes) <- config.normal) {
    //   val jsName = s"${dir.toString}/test/$filename".replace("//", "/")
    //   val name = removedExt(jsName).drop(dir.toString.length)
    //   check("Test262Eval", name, {
    //     val includeList = includes.foldLeft(initStList) {
    //       case (li, s) => for {
    //         x <- li
    //         y <- includeMap(s)
    //       } yield x ++ y
    //     } match {
    //       case Right(l) => l
    //       case Left(msg) => throw NotSupported(msg)
    //     }

    //     val jsConfig = aseConfig.copy(fileNames = List(jsName))
    //     val ast = Parse((), jsConfig)
    //     ModelHelper.checkSupported(ast)

    //     val stList = includeList ++ ModelHelper.flattenStatement(ast)
    //     val st = IREval(Load(ModelHelper.mergeStatement(stList), jsConfig), jsConfig, evalConfig)
    //     evalJSTest(st)
    //   })
    // }
  }

  init
}

// TODO
// class Test262LongTest extends Test262Test {
//   override def testKind = Long
// }
//
// class Test262VeryLongTest extends Test262Test {
//   override def testKind = VeryLong
// }
//
// class Test262ManualTest extends Test262Test {
//   override def testKind = Manual
// }
