package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase._
import kr.ac.kaist.ase.{ LINE_SEP, ASEConfig }
import kr.ac.kaist.ase.parser.{ MetaParser, MetaData }
import kr.ac.kaist.ase.util.Useful._
import java.io._

import spray.json._
import kr.ac.kaist.ase.util._
import kr.ac.kaist.ase.util.TestConfigJsonProtocol._

// FilterMeta phase
case object FilterMeta extends PhaseObj[Unit, FilterMetaConfig, Unit] {
  val name = "extract-meta"
  val help = "Extracts metadata"
  val standardFeatures = List(
    "ArrayBuffer",
    "Array.prototype.values",
    "arrow-function",
    "async-iteration",
    "async-functions",
    "Atomics",
    "caller",
    "class",
    "computed-property-names",
    "const",
    "cross-realm",
    "DataView",
    "DataView.prototype.getFloat32",
    "DataView.prototype.getFloat64",
    "DataView.prototype.getInt16",
    "DataView.prototype.getInt32",
    "DataView.prototype.getInt8",
    "DataView.prototype.getUint16",
    "DataView.prototype.getUint32",
    "DataView.prototype.setUint8",
    "default-parameters",
    "destructuring-assignment",
    "destructuring-binding",
    "for-of",
    "Float32Array",
    "Float64Array",
    "generators",
    "Int8Array",
    "Int32Array",
    "json-superset",
    "let",
    "Map",
    "new.target",
    "object-rest",
    "object-spread",
    "Object.is",
    "optional-catch-binding",
    "Promise.prototype.finally",
    "Proxy",
    "Reflect",
    "Reflect.construct",
    "Reflect.set",
    "Reflect.setPrototypeOf",
    "regexp-dotall",
    "regexp-lookbehind",
    "regexp-named-groups",
    "regexp-unicode-property-escapes",
    "Set",
    "SharedArrayBuffer",
    "String.fromCodePoint",
    "String.prototype.endsWith",
    "String.prototype.includes",
    "super",
    "Symbol",
    "Symbol.asyncIterator",
    "Symbol.hasInstance",
    "Symbol.isConcatSpreadable",
    "Symbol.iterator",
    "Symbol.match",
    "Symbol.replace",
    "Symbol.search",
    "Symbol.species",
    "Symbol.split",
    "Symbol.toPrimitive",
    "Symbol.toStringTag",
    "Symbol.unscopables",
    "tail-call-optimization",
    "template",
    "TypedArray",
    "u180e",
    "Uint8Array",
    "Uint16Array",
    "Uint8ClampedArray",
    "WeakMap",
    "WeakSet"
  )

  def apply(
    unit: Unit,
    aseConfig: ASEConfig,
    config: FilterMetaConfig
  ): Unit = {

    val test262Dir = s"$TEST_DIR/test262"
    val dir = new File(test262Dir)
    val (normalL, errorL) = walkTree(dir).toList.filter(
      (file) => jsFilter(file.getName)
    ).map((x) => MetaParser(x.toString, dir.toString)).filter((x) => filter(x)).map {
        case MetaData(name, n, _, i, _, _) => Test262Config(name, n, i)
      }.partition(_.negative.isEmpty)
    val summary = Test262ConfigSummary(
      normalL.map((x) => NormalTestConfig(x.name, x.includes)),
      errorL.collect { case Test262Config(name, Some(n), in) => ErrorTestConfig(name, n, in) }
    )
    val pw = new PrintWriter(new File(s"$TEST_DIR/test262.json"))
    pw.println(summary.toJson.prettyPrint)
    pw.close()
  }

  def filter(meta: MetaData) = !(
    (meta.flags contains "noStrict") ||
    (meta.flags contains "raw") ||
    (meta.flags contains "module") ||
    (meta.flags contains "CanBlockIsFalse") ||
    (meta.flags contains "CanBlockIsTrue")
  ) &&
    (meta.locales.isEmpty) &&
    ((meta.name startsWith "/test/language/") ||
      (meta.name startsWith "/test/built-ins/")) &&
      !((meta.name startsWith "/test/language/module-code/") ||
        (meta.name startsWith "/test/language/expressions/dynamic-import/") ||
        (meta.name startsWith "/test/language/expressions/import.meta/")) &&
      (meta.features.forall(standardFeatures contains _))
  def defaultConfig: FilterMetaConfig = FilterMetaConfig()
  val options: List[PhaseOption[FilterMetaConfig]] = List()
}

// FilterMeta phase config
case class FilterMetaConfig() extends Config

