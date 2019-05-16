package kr.ac.kaist.ase.phase

import java.io.File
import kr.ac.kaist.ase.ASEConfig
import kr.ac.kaist.ase.algorithm._
import kr.ac.kaist.ase.core
import kr.ac.kaist.ase.spec._
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.util._
import scala.io.Source
import spray.json._

// GenModel phase
case object GenModel extends PhaseObj[Unit, GenModelConfig, Unit] {
  import DefaultJsonProtocol._
  implicit val TyFormat = jsonFormat2(Ty)
  implicit val RhsFormat = jsonFormat1(Rhs)
  implicit val ProductionFormat = jsonFormat3(Production)
  implicit val GrammarFormat = jsonFormat1(Grammar)
  implicit val SpecFormat = jsonFormat3(Spec)

  val name: String = "gen-model"
  val help: String = "generates ECMAScript models."

  def apply(
    non: Unit,
    aseConfig: ASEConfig,
    config: GenModelConfig
  ): Unit = {
    val version = getFirstFilename(aseConfig, "gen-model")
    val json = readFile(s"./src/main/resources/$version/spec.json")
    val spec = json.parseJson.convertTo[Spec]

    def loadEFunFromFile(name: String): core.Func = {
      val f = Algorithm(fileReader(s"./src/main/resources/$version/algorithm/$name.algorithm"))
      RuleCompiler(f)
    }
    val nt = spec.globalMethods.map(name => name -> loadEFunFromFile(name)).toMap
    nt.foreach {
      case (name, func) => {
        val nf = getPrintWriter(s"./src/main/scala/kr/ac/kaist/ase/model/$name.scala")
        nf.println(s"package kr.ac.kaist.ase.model")
        nf.println
        nf.println(s"import kr.ac.kaist.ase.core._")
        nf.println(s"object $name {")
        nf.println(s"  val func: Func = $func")
        nf.println(s"}")
        nf.close()
      }
    }
    val nf = getPrintWriter("./src/main/scala/kr/ac/kaist/ase/model/Global.scala")
    nf.println("package kr.ac.kaist.ase.model")
    nf.println
    nf.println("import kr.ac.kaist.ase.core._")
    nf.println("object Global {")
    nf.println("  val initGlobal: Map[Id, Value] = Map(")
    nf.println(nt.map {
      case (i, _) => (new StringContext("(Id(\"", "\") -> ", ".func)").s(i, i))
    }.mkString(","))
    nf.println(")")
    nf.println("}")
    nf.close()
  }

  def defaultConfig: GenModelConfig = GenModelConfig()
  val options: List[PhaseOption[GenModelConfig]] = List()
}

// GenModel phase config
case class GenModelConfig() extends Config
