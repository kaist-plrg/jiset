package kr.ac.kaist.jiset.generator

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.BugPatch._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.spec._
import java.io.File

object ModelGenerator {
  def apply(packageName: String, modelDir: String, spec: Spec): Unit = {
    // make model directories
    mkdir(modelDir)
    mkdir(s"$modelDir/ast")
    mkdir(s"$modelDir/type")
    mkdir(s"$modelDir/algorithm")

    val methods = spec.globalMethods
    val builtinMethods = spec.globalMethods.filter {
      case m => m.startsWith("GLOBAL.") && m != "GLOBAL.AsyncFunction"
    }
    val symbols = spec.symbols
    val intrinsics = spec.intrinsics
    val consts = (spec.consts.toSet - "[empty]" + "emptySyntax").toList
    val grammar = spec.grammar
    val tys = spec.tys

    List(
      "BaseGlobal",
      "BaseHeap",
      "BaseType",
      "BuiltinHeap",
      "ESValueParser",
      "ModelHelper"
    ).foreach(filename => {
        val nf = getPrintWriter(s"$modelDir/$filename.scala")
        nf.println(s"package $packageName.model")
        nf.println
        nf.println(s"import $packageName.AST")
        nf.println(s"import $packageName.ir._")
        nf.println(s"import $packageName.error._")
        nf.println(s"import $packageName.util.Useful._")
        nf.println(s"import $packageName.parser.UnicodeRegex")
        nf.println
        nf.print(readFile(s"$RESOURCE_DIR/$VERSION/manual/$filename.scala"))
        nf.close()
      })

    GrammarGenerator(packageName, modelDir, grammar)
    tys.foreach { case ((tname, methods)) => TypeGenerator(packageName, modelDir, tname, methods) }

    methods.foreach(name => MethodGenerator(packageName, modelDir, name))
    for (file <- walkTree(s"$RESOURCE_DIR/$VERSION/manual/algorithm")) {
      val filename = file.getName
      if (scalaFilter(filename)) {
        val name = file.toString
        val nf = getPrintWriter(s"$modelDir/algorithm/$filename")
        nf.println(s"package $packageName.model")
        nf.println
        nf.println(s"import $packageName.Algorithm")
        nf.println(s"import $packageName.ir._")
        nf.println(s"import $packageName.ir.Parser._")
        nf.println
        if (!noIsFunctionDefinition && filename == "FunctionExpression0IsFunctionDefinition0.scala") nf.println(readFile(removedExt(name) + ".error"))
        else nf.print(readFile(name))
        nf.close()
      } else deleteFile(s"$modelDir/algorithm/$filename")
    }

    val nf = getPrintWriter(s"$modelDir/Model.scala")
    nf.println(s"""package $packageName.model""")
    nf.println(s"""""")
    nf.println(s"""import $packageName.ir._""")
    nf.println(s"""import $packageName.util.Useful._""")
    nf.println(s"""""")
    nf.println(s"""object Model {""")
    nf.println(s"""  lazy val initState: State = State(""")
    nf.println(s"""    context = Context(),""")
    nf.println(s"""    globals = initGlobal,""")
    nf.println(s"""    heap = initHeap""")
    nf.println(s"""  )""")
    nf.println(s"""  lazy val initGlobal: Map[Id, Value] = Map(""")
    symbols.foreach {
      case (k, v) =>
        nf.println(s"""    Id("${getScalaName(k)}") -> NamedAddr("GLOBAL.$v"),""")
    }
    intrinsics.foreach {
      case (k, v) =>
        nf.println(s"""    Id("${getScalaName(k)}") -> NamedAddr("$v"),""")
    }
    nf.println(methods.map(getScalaName _).map(x => s"""    Id("$x") -> $x.func""").mkString("," + LINE_SEP))
    nf.println(s"""  ) ++ Map(""")
    nf.println(consts.map(i =>
      s"""      Id("CONST_$i") -> NamedAddr("CONST_$i")""").mkString("," + LINE_SEP))
    nf.println(s"""    ) ++ BaseGlobal.get""")
    nf.println(s"""  lazy val builtinMethods: List[(String, Int, Func)] = List(""")
    nf.println(builtinMethods.map(x => {
      val obj = getScalaName(x)
      s"""    ("$x", $obj.length, $obj.func)"""
    }).mkString("," + LINE_SEP))
    nf.println(s"""  )""")
    nf.println(s"""  lazy val initHeap: Heap = Heap(BaseHeap.get ++ BuiltinHeap.get ++ Map(""")
    nf.println(consts.map(i =>
      s"""    NamedAddr("CONST_$i") -> IRSymbol(Str("CONST_$i"))""").mkString("," + LINE_SEP))
    nf.println(s"""  ) match {""")
    nf.println(s"""      case m => ModelHelper.addBuiltin(m, BuiltinHeap.builtinMethods ++ builtinMethods)""")
    nf.println(s"""    })""")
    nf.println(s"""  lazy val tyMap: Map[String, Map[Value, Value]] = Map(""")
    nf.println(tys.map {
      case ((tname, _)) => s"""    ("$tname" -> $tname.map)"""
    }.mkString("," + LINE_SEP))
    nf.println(s"""  )""")
    nf.println(s"""}""")
    nf.close()
  }
}
