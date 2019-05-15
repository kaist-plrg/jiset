package kr.ac.kaist.ase.phase

import scala.util.{ Try, Success }
import kr.ac.kaist.ase.ASEConfig
import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.algorithm._
import kr.ac.kaist.ase.util.Useful.fileReader
import kr.ac.kaist.ase.util._
import java.io.{ PrintWriter, File }
import scala.io.Source
import spray.json._
import DefaultJsonProtocol._ // if you don't supply your own Protocol (see below)

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val typestructureFormat = jsonFormat2(TypeStructure)
  implicit val childstructureFormat = jsonFormat2(ChildStructure)
  implicit val synstructureFormat = jsonFormat3(SyntaxStructure)
  implicit val structureFormat = jsonFormat3(Structure)
}
import MyJsonProtocol._

// LoadScript phase
case object LoadGlobal extends PhaseObj[Unit, LoadGlobalConfig, Unit] {
  val name: String = "load-algo"
  val help: String = "Create load-algorithm scala file "

  def loadEFunFromFile(i: String) = {
    val f = Algorithm(fileReader(s"./src/main/resources/es2018/algorithm/${i}.algorithm"))
    RuleCompiler(f)
  }
  def apply(
    non: Unit,
    aseConfig: ASEConfig,
    config: LoadGlobalConfig
  ): Unit = {
    val f = Source.fromFile("./src/main/resources/es2018/step.json").mkString
    val Structure(t, s, ty) = f.parseJson.convertTo[Structure]
    val nt = t.map((x) => (x, loadEFunFromFile((x)))).toMap
    nt.foreach {
      case (i, j) => {
        val nf = new PrintWriter(new File(s"./src/main/scala/kr/ac/kaist/ase/model/${i}.scala"))
        nf.println("package kr.ac.kaist.ase.model")
        nf.println("import kr.ac.kaist.ase.core._")
        nf.println(s"object ${i} {")
        nf.println(s"val f: Value = ${j}")
        nf.println("}")
        nf.close()
      }
    }
    val nf = new PrintWriter(new File(s"./src/main/scala/kr/ac/kaist/ase/model/Global.scala"))
    nf.println("package kr.ac.kaist.ase.model")
    nf.println("import kr.ac.kaist.ase.core._")
    nf.println("object Global {")
    nf.println("val initGlobal: Map[Id, Value] = Map(")
    nf.println(nt.map {
      case (i, _) => (new StringContext("(Id(\"", "\") -> ", ".f)").s(i, i))
    }.mkString(","))
    nf.println(")")
    nf.println("}")
    nf.close()
  }

  def defaultConfig: LoadGlobalConfig = LoadGlobalConfig()
  val options: List[PhaseOption[LoadGlobalConfig]] = List()
}

// LoadCore phase config
case class LoadGlobalConfig() extends Config

case class Structure(top: List[String], syntax: List[SyntaxStructure], ty: List[TypeStructure])

case class SyntaxStructure(name: String, childs: List[ChildStructure], algo: List[String])

case class ChildStructure(name: String, structure: String)

case class TypeStructure(name: String, list: List[String])