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

    def modelForSingleMethod(methodName: String, methodFileName: String): Unit = {
      val func = loadEFunFromFile(methodFileName)
      val nf = getPrintWriter(s"./src/main/scala/kr/ac/kaist/ase/model/$methodName.scala")
      nf.println(s"package kr.ac.kaist.ase.model")
      nf.println
      nf.println(s"import kr.ac.kaist.ase.core._")
      nf.println(s"object $methodName {")
      nf.println(s"  val func: Func = $func")
      nf.println(s"}")
      nf.close()
    }
    def modelForMethods(methods: List[String]): Unit = {
      methods foreach ((i) => modelForSingleMethod(i, i))
      val nf = getPrintWriter("./src/main/scala/kr/ac/kaist/ase/model/Global.scala")
      nf.println("package kr.ac.kaist.ase.model")
      nf.println
      nf.println("import kr.ac.kaist.ase.core._")
      nf.println("object Global {")
      nf.println("  val initGlobal: Map[Id, Value] = Map(")
      nf.println(methods.map(
        (i) => (new StringContext("(Id(\"", "\") -> ", ".func)").s(i, i))
      ).mkString(","))
      nf.println(")")
      nf.println("}")
      nf.close()
    }

    def modelForSingleType(ty: Ty): Unit = {
      ty.methods foreach ((i) => modelForSingleMethod(s"${ty.name}_${i}", s"${ty.name}.${i}"))

      val nf = getPrintWriter(s"./src/main/scala/kr/ac/kaist/ase/model/${ty.name}.scala")
      nf.println(s"package kr.ac.kaist.ase.model")
      nf.println
      nf.println(s"import kr.ac.kaist.ase.core._")
      nf.println(s"object ${ty.name} {")
      nf.println(s"  val obj: Obj = Obj(")
      nf.println(new StringContext("  Ty(\"", "\"),").s(ty.name))
      nf.println(s"  Map(),")
      nf.println(s"  Map(")
      nf.println(ty.methods.map(
        (i) => (new StringContext("(\"", "\" -> ", "_", ".func)").s(i, ty.name, i))
      ).mkString(","))
      nf.println("))")
      nf.println(s"}")
      nf.close()
    }

    def modelForTypes(tys: List[Ty]): Unit = {
      tys foreach modelForSingleType
      val nf = getPrintWriter("./src/main/scala/kr/ac/kaist/ase/model/GlobalType.scala")
      nf.println("package kr.ac.kaist.ase.model")
      nf.println
      nf.println("import kr.ac.kaist.ase.core._")
      nf.println("object GlobalType {")
      nf.println("  val initType: Map[String, Obj] = Map(")
      nf.println(tys.map(
        (i) => (new StringContext("(\"", "\" -> ", ".obj)").s(i.name, i.name))
      ).mkString(","))
      nf.println(")")
      nf.println("}")
      nf.close()

    }

    modelForMethods(spec.globalMethods)
    modelForTypes(spec.tys)
  }

  def defaultConfig: GenModelConfig = GenModelConfig()
  val options: List[PhaseOption[GenModelConfig]] = List()
}

// GenModel phase config
case class GenModelConfig() extends Config
