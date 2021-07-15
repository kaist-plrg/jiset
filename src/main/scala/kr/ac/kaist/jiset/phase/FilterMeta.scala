package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.{ LINE_SEP, JISETConfig }
import kr.ac.kaist.jiset.parser.{ MetaParser, MetaData }
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.JvmUseful._
import java.io._

import io.circe._, io.circe.syntax._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.TestConfigJsonProtocol._

// FilterMeta phase
case object FilterMeta extends PhaseObj[Unit, FilterMetaConfig, Test262ConfigSummary] {
  val name = "extract-meta"
  val help = "extracts and filters out metadata of test262 tests."

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
    "Int16Array",
    "Int32Array",
    "json-superset",
    "let",
    "Map",
    "new.target",
    "object-rest",
    "object-spread",
    "Object.is",
    "optional-catch-binding",
    "Promise",
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
    "rest-parameters",
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
    "Uint32Array",
    "Uint8ClampedArray",
    "WeakMap",
    "WeakSet",
  // XXX: See appendix B Additional ECMAScript Features for Web Browsers
  // "__proto__",
  // "__getter__",
  // "__setter__",
  )

  lazy val test262Dir = new File(s"$TEST_DIR/test262/test")
  lazy val allTests = TestList(
    walkTree(test262Dir)
      .toList
      .filter(f => jsFilter(f.getName))
      .map(x => MetaParser(x.toString))
      .sorted
  )

  lazy val manualNonstrict = List(
    "language/eval-code/indirect/always-non-strict",
    "language/eval-code/indirect/non-definable-global-function",
    "language/eval-code/indirect/non-definable-global-generator",
    "language/eval-code/indirect/non-definable-global-var",
    "language/eval-code/indirect/var-env-func-init-global-new",
    "language/eval-code/indirect/var-env-func-init-global-update-configurable",
    "language/eval-code/indirect/var-env-func-init-multi",
    "language/eval-code/indirect/var-env-func-non-strict",
    "language/eval-code/indirect/var-env-global-lex-non-strict",
    "language/eval-code/indirect/var-env-var-init-global-exstng",
    "language/eval-code/indirect/var-env-var-init-global-new",
    "language/eval-code/indirect/var-env-var-non-strict",
    "language/statements/variable/12.2.1-21-s"
  )

  lazy val manualEarlyError = List(
    "language/arguments-object/10.5-1-s",
    "language/arguments-object/10.5-7-b-1-s",
    "language/eval-code/direct/new.target",
    "language/eval-code/direct/new.target-arrow",
    "language/eval-code/direct/parse-failure-3",
    "language/eval-code/direct/parse-failure-4",
    "language/eval-code/direct/super-call",
    "language/eval-code/direct/super-call-arrow",
    "language/eval-code/direct/super-call-fn",
    "language/eval-code/direct/super-call-method",
    "language/eval-code/direct/super-prop",
    "language/eval-code/direct/super-prop-arrow",
    "language/eval-code/direct/super-prop-dot-no-home",
    "language/eval-code/direct/super-prop-expr-no-home",
    "language/eval-code/direct/super-prop-expr-no-home-no-eval",
    "language/eval-code/indirect/new.target",
    "language/eval-code/indirect/parse-failure-3",
    "language/eval-code/indirect/parse-failure-4",
    "language/eval-code/indirect/super-call",
    "language/eval-code/indirect/super-prop",
    "language/expressions/call/eval-strictness-inherit-strict",
    "language/statements/break/S12.8_A7",
    "language/statements/continue/S12.7_A7",
    "language/statements/function/13.0-8-s",
    "language/statements/function/13.1-2-s",
    "language/statements/function/13.1-4-s",
    "language/statements/try/catch-parameter-boundnames-restriction-arguments-eval-throws",
    "language/statements/try/catch-parameter-boundnames-restriction-eval-eval-throws",
    "language/statements/variable/12.2.1-18-s",
    "language/statements/variable/12.2.1-19-s",
    "language/statements/variable/12.2.1-2-s",
    "language/statements/variable/12.2.1-22-s",
    "language/statements/variable/12.2.1-3-s",
    "language/statements/variable/12.2.1-4-s",
    "language/statements/variable/12.2.1-7-s",
    "language/statements/variable/12.2.1-8-s",
    "language/statements/with/12.10.1-10-s",
    "language/statements/with/12.10.1-11-s",
    "language/statements/with/12.10.1-14-s",
    "language/statements/with/12.10.1-15-s",
    "language/statements/with/12.10.1-16-s",
    "language/statements/with/12.10.1-7-s"
  )

  lazy val manualInprogress = List(
    ("built-ins/String/prototype/matchAll/length", "matchAll"),
    ("built-ins/String/prototype/matchAll/name", "matchAll"),
    ("built-ins/String/prototype/matchAll/prop-desc", "matchAll"),
    ("built-ins/String/prototype/matchAll/regexp-is-null", "matchAll"),
    ("built-ins/String/prototype/matchAll/regexp-is-undefined", "matchAll"),
    ("built-ins/String/prototype/matchAll/regexp-matchAll-invocation", "matchAll"),
    ("built-ins/String/prototype/matchAll/this-val-non-obj-coercible", "matchAll"),
    ("built-ins/String/prototype/matchAll/regexp-matchAll-not-callable", "matchAll"),
    ("built-ins/String/prototype/matchAll/regexp-prototype-has-no-matchAll", "matchAll"),
    ("built-ins/RegExp/lookBehind/sliced-strings", "substr")
  )

  lazy val longTest = List(
    "built-ins/Array/prototype/Symbol.unscopables/value",
    "built-ins/Array/prototype/concat/Array.prototype.concat_spreadable-sparse-object",
    "built-ins/Array/prototype/every/15.4.4.16-7-c-ii-2",
    "built-ins/Array/prototype/filter/15.4.4.20-9-c-ii-1",
    "built-ins/Array/prototype/flatMap/array-like-objects",
    "built-ins/Array/prototype/forEach/15.4.4.18-7-c-ii-1",
    "built-ins/Array/prototype/indexOf/15.4.4.14-10-1",
    "built-ins/Array/prototype/lastIndexOf/15.4.4.15-9-1",
    "built-ins/Array/prototype/map/15.4.4.19-8-c-ii-1",
    "built-ins/Array/prototype/some/15.4.4.17-7-c-ii-2",
    "built-ins/RegExp/S15.10.2.8_A3_T15",
    "built-ins/RegExp/S15.10.2.8_A3_T16",
    "built-ins/RegExp/property-escapes/generated/Changes_When_NFKC_Casefolded",
    "built-ins/RegExp/property-escapes/generated/General_Category_-_Letter",
    "built-ins/RegExp/property-escapes/generated/General_Category_-_Other",
    "built-ins/RegExp/property-escapes/generated/General_Category_-_Unassigned",
    "built-ins/RegExp/property-escapes/generated/ID_Continue",
    "built-ins/RegExp/property-escapes/generated/ID_Start",
    "language/expressions/call/tco-call-args",
    "language/expressions/call/tco-member-args",
    "language/expressions/class/async-gen-method-static/yield-star-async-throw",
    "language/expressions/comma/tco-final",
    "language/expressions/conditional/tco-cond",
    "language/expressions/conditional/tco-pos",
    "language/expressions/logical-and/tco-right",
    "language/expressions/logical-or/tco-right",
    "language/expressions/tco-pos",
    "language/reserved-words/ident-name-keyword-accessor",
    "language/reserved-words/ident-name-keyword-prop-name",
    "language/statements/block/tco-stmt",
    "language/statements/block/tco-stmt-list",
    "language/statements/do-while/tco-body",
    "language/statements/for/tco-const-body",
    "language/statements/for/tco-let-body",
    "language/statements/for/tco-lhs-body",
    "language/statements/for/tco-var-body",
    "language/statements/if/tco-else-body",
    "language/statements/if/tco-if-body",
    "language/statements/labeled/tco",
    "language/statements/return/tco",
    "language/statements/switch/tco-case-body",
    "language/statements/switch/tco-case-body-dflt",
    "language/statements/switch/tco-dftl-body",
    "language/statements/try/tco-catch",
    "language/statements/try/tco-catch-finally",
    "language/statements/try/tco-finally",
    "language/statements/while/tco-body",
    "language/types/number/8.5.1",
  )

  lazy val veryLongTest = List(
    "built-ins/Array/length/S15.4.5.2_A3_T4",
  )

  def getTests(features: List[String]): TestList = allTests
    .remove("harness", _.name startsWith "harness")
    .remove("internationalisation", _.name startsWith "intl")
    .remove("annex", _.name startsWith "annex")
    .remove("in-progress features", m => (!m.features.forall(features contains _)) || (manualInprogress.map(_._1) contains removedExt(m.name)))
    .remove("non-strict", m => (m.flags contains "noStrict") || (m.flags contains "raw") || (manualNonstrict contains removedExt(m.name)))
    .remove("module", m => (
      (m.flags contains "module") ||
      (m.name startsWith "language/module-code/") ||
      (m.name startsWith "language/import/") ||
      (m.name startsWith "language/expressions/dynamic-import/") ||
      (m.name startsWith "language/expressions/import.meta/")
    ))
    .remove("early errors", m => !m.negative.isEmpty || (manualEarlyError contains removedExt(m.name)))
    .remove("inessential built-in objects", m => (
      (m.flags contains "CanBlockIsFalse") ||
      (m.flags contains "CanBlockIsTrue") ||
      !m.locales.isEmpty
    ))

  lazy val test262configSummary = getTests(standardFeatures)
    .remove("longTest", m => longTest contains removedExt(m.name))
    .remove("veryLongTest", m => veryLongTest contains removedExt(m.name))
    .getSummary

  lazy val test262LongconfigSummary = getTests(standardFeatures)
    .remove("non longTest", m => !(longTest contains removedExt(m.name)))
    .getSummary

  lazy val test262VeryLongconfigSummary = getTests(standardFeatures)
    .remove("non veryLongTest", m => !(veryLongTest contains removedExt(m.name)))
    .getSummary

  lazy val test262ManualconfigSummary =
    readJson[Test262ConfigSummary](s"$TEST_DIR/test262.json")

  def apply(
    unit: Unit,
    jisetConfig: JISETConfig,
    config: FilterMetaConfig
  ): Test262ConfigSummary = showTime("extracting and filtering out metadata of test262 tests", {
    println(s"Total ${allTests.length} tests")
    val summary = test262configSummary
    println(s"negative applicable tests: ${summary.error.length}")
    println(s"positive applicable tests: ${summary.normal.length}")
    summary
  })
  def defaultConfig: FilterMetaConfig = FilterMetaConfig()
  val options: List[PhaseOption[FilterMetaConfig]] = List()
}

// FilterMeta phase config
case class FilterMetaConfig() extends Config
