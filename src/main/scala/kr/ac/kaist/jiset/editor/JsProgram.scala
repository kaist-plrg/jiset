package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.{ cfg => CFG, _ }
import kr.ac.kaist.jiset.ir.{ State, NodeCursor, Interp }
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.parser.{ MetaParser, BasicParsers }
import kr.ac.kaist.jiset.util.JvmUseful._

// js program in filtered mapping
trait JsProgram extends EditorElem {
  // unique id
  def uid: String

  // AST of js program
  val script: Script

  // meta-data of js program (id, name)
  val meta: (String, Int)
  val (name, id) = meta
  def filename: String

  // touched nodes
  lazy val touched: Array[Boolean] = {
    val cached = s"$EDITOR_LOG_DIR/cached/$uid.json"

    // check if cached result exists
    if (exists(cached)) {
      // read touched from cached
      readJson[Array[Boolean]](cached)
    } else {
      // run interp and dump touched result
      val touched = Array.fill(cfg.nodes.size)(false)
      val interp = new Interp(initState, useHook = true)

      // subscribe step event in interp
      interp.subscribe(Interp.Event.Step, { st =>
        st.context.cursorOpt.get match {
          case NodeCursor(n) => { touched(n.uid) = true }
          case _ =>
        }
      })

      // fixpoint
      interp.fixpoint

      // cache touched result
      dumpJson(touched, cached, true)
      touched
    }
  }

  // raw string of js program
  lazy val raw: String = script.toString

  // size of js program
  def size: Int = raw.length

  // initialize state
  def initState: State =
    Initialize(script, Some(filename), NodeCursor)

  // parse script from filename
  def fromFile(filename: String): Script =
    Parser.parse(Parser.Script(Nil), fileReader(filename)).get

  // toString
  override def toString: String = s"[$uid]: $filename ($size)"

  // equals
  override def equals(that: Any): Boolean = that match {
    case that: JsProgram => this.uid == that.uid
    case _ => false
  }

  // override hashCode
  override def hashCode: Int = uid.hashCode
}

// test262 program
case class Test262Program(meta: (String, Int)) extends JsProgram {
  // unique id
  def uid: String = s"T$id"

  // filename
  override def filename: String = s"$TEST262_TEST_DIR/$name"

  // AST of js program
  lazy val script = Test262.loadTest(
    fromFile(filename),
    MetaParser(filename).includes
  )
}

// jest program
case class JestProgram(meta: (String, Int)) extends JsProgram {
  // unique id
  def uid: String = s"J$id"

  // filename
  override def filename: String = s"$DATA_DIR/jest/$name"

  // AST of js program
  lazy val script = fromFile(filename)
}

// custom program
case class CustomProgram(meta: (String, Int)) extends JsProgram {
  // unique id
  def uid: String = s"C$id"

  // filename
  override def filename: String = s"$DATA_DIR/custom/$name"

  // AST of js program
  lazy val script = fromFile(filename)
}
// case class Generated(id, Int, filename: String) extends JsProgram
// case class Reduced(uid, Int,  extends JsProgram

// js program parser
object JsProgramParser extends BasicParsers {
  implicit lazy val program: Parser[JsProgram] =
    ("[uid: " ~> ("\\w".r ~ int) <~ "]") ~ "[\\w-.\\/]+".r ^^ {
      case (kind ~ id) ~ name =>
        val meta = (name, id)
        kind match {
          case "T" => Test262Program(meta)
          case "J" => JestProgram(meta)
          case "C" => CustomProgram(meta)
        }
    }
  def apply(str: String): JsProgram = parse(str)
}
