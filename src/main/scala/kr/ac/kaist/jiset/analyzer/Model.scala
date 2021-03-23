package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.Beautifier._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.algorithm.{ Algo, Head, SyntaxDirectedHead }
import kr.ac.kaist.jiset.util.Useful._
import scala.Console._
import scala.util.matching.Regex

// TODO more manual modelings
class Model(cfg: CFG) {
  // global variables and heaps
  lazy val global: (Map[String, AbsValue], Map[Loc, AbsObj]) = getGlobal

  // type map
  lazy val typeMap: Map[String, TyInfo] =
    typeInfos.map(info => info.name -> info).toMap

  //////////////////////////////////////////////////////////////////////////////
  // Private Helper Functions
  //////////////////////////////////////////////////////////////////////////////
  // get global variables and heaps
  private def getGlobal = {
    val (env, heaps) = cfg.getGlobal
    val globalEnv = manualEnv ++ (for ((x, v) <- env) yield x -> AbsValue(v))
    var globalHeap = (for ((a, o) <- heaps) yield a -> AbsObj(o)).toMap

    // map structures
    for ((x, (p, m)) <- manualMaps) {
      val map: Map[String, AbsObj.MapD.AbsVOpt] = m.map {
        case (k, v) => k -> AbsObj.MapD.AbsVOpt(v, AbsAbsent.Bot)
      }
      val pair = NamedAddr(x) -> AbsObj.MapElem(p, AbsObj.MapD(map, AbsObj.MapD.AbsVOpt(None)))
      globalHeap += pair
    }

    // list structures
    for ((x, v) <- manualLists) {
      val pair = (NamedAddr(x) -> AbsObj.ListElem(AbsObj.ListD.ListElem(v)))
      globalHeap += pair
    }

    (globalEnv, globalHeap)
  }

  // TODO more manual modelings
  private def typeInfos: List[TyInfo] = List(
    TyInfo(
      name = "RealmRecord",
      "Intrinsics" -> AbsValue(NamedAddr("Intrinsics")),
      "GlobalObject" -> AbsValue(NamedAddr("Global")),
      "GlobalEnv" -> AbsTy("EnvironmentRecord"),
      "TemplateMap" -> AbsValue(NamedAddr("TemplateMap")),
      "HostDefined" -> AbsUndef.Top,
    ),
    TyInfo(
      name = "TemplatePair",
      "Site" -> AbsValue(ASTVal("TemplateLiteral")),
      "Array" -> AbsValue(Ty("Object"))
    ),
    // modules
    TyInfo(
      name = "ScriptRecord",
      "Realm" -> AbsValue(Ty("RealmRecord")) ⊔ AbsUndef.Top,
      "Environment" -> AbsValue(Ty("EnvironmentRecord")) ⊔ AbsUndef.Top,
      "ECMAScriptCode" -> AbsValue(ASTVal("AnyNode")),
      "HostDefined" -> emptyConst,
    ),
    TyInfo(
      name = "ModuleRecord",
      "Realm" -> AbsValue(Ty("RealmRecord")) ⊔ AbsUndef.Top,
      "Environment" -> AbsValue(Ty("EnvironmentRecord")) ⊔ AbsUndef.Top,
      "Namespace" -> AbsValue(Ty("Object")) ⊔ AbsUndef.Top,
      "HostDefined" -> emptyConst,
    ),
    TyInfo(
      name = "CyclicModuleRecord",
      parent = "ModuleRecord",
      "Status" -> (AbsConst("unlinked", "linking", "linked", "evaluating", "evaluated"): AbsValue),
      "EvaluationError" -> AbsValue(Undef) ⊔ AbsComp.Top.abrupt,
      "DFSIndex" -> AbsValue(Undef) ⊔ AbsNum.Top,
      "DFSAncestorIndex" -> AbsValue(Undef) ⊔ AbsNum.Top,
      "RequestedModules" -> AbsValue(NamedAddr("StringList")),
    ),
    TyInfo(
      name = "SourceTextModuleRecord",
      parent = "CyclicModuleRecord",
      "ECMAScriptCode" -> AbsValue(ASTVal("AnyNode")),
      "Context" -> AbsValue(Ty("ExecutionContext")),
      "ImportMeta" -> AbsValue(Ty("Object")) ⊔ emptyConst,
      "ImportEntries" -> AbsValue(NamedAddr("ImportEntries")),
      "LocalExportEntries" -> AbsValue(NamedAddr("ExportEntries")),
      "IndirectExportEntries" -> AbsValue(NamedAddr("ExportEntries")),
      "StarExportEntries" -> AbsValue(NamedAddr("ExportEntries")),
    ),
    TyInfo(
      name = "ImportEntryRecord",
      "ModuleRequest" -> AbsValue(AbsStr.Top),
      "ImportName" -> AbsValue(AbsStr.Top),
      "LocalName" -> AbsValue(AbsStr.Top),
    ),
    TyInfo(
      name = "ExportEntryRecord",
      "ExportName" -> AbsValue(AbsStr.Top) ⊔ AbsNull.Top,
      "ModuleRequest" -> AbsValue(AbsStr.Top) ⊔ AbsNull.Top,
      "ImportName" -> AbsValue(AbsStr.Top) ⊔ AbsNull.Top,
      "LocalName" -> AbsValue(AbsStr.Top) ⊔ AbsNull.Top,
    ),
    // execution contexts
    TyInfo(
      name = "ExecutionContext",
      "Function" -> AbsValue(Ty("Object")) ⊔ AbsNull.Top,
      "Realm" -> AbsValue(Ty("RealmRecord")),
      "ScriptOrModule" -> AbsTy(Ty("ScriptRecord"), Ty("ModuleRecord")),
      "Generator" -> AbsTy(Ty("OrdinaryObject")),
      "LexicalEnvironment" -> AbsValue(Ty("EnvironmentRecord")),
      "VariableEnvironment" -> AbsValue(Ty("EnvironmentRecord")),
    ),
    // environment records
    TyInfo(
      name = "EnvironmentRecord",
      "HasBinding" -> getClos(""".*\.HasBinding""".r),
      "CreateMutableBinding" -> getClos(""".*\.CreateMutableBinding""".r),
      "CreateImmutableBinding" -> getClos(""".*\.CreateImmutableBinding""".r),
      "InitializeBinding" -> getClos(""".*\.InitializeBinding""".r),
      "SetMutableBinding" -> getClos(""".*\.SetMutableBinding""".r),
      "GetThisBinding" -> getClos(""".*\.GetThisBinding""".r),
      "DeleteBinding" -> getClos(""".*\.DeleteBinding""".r),
      "HasThisBinding" -> getClos(""".*\.HasThisBinding""".r),
      "HasSuperBinding" -> getClos(""".*\.HasSuperBinding""".r),
      "WithBaseObject" -> getClos(""".*\.WithBaseObject""".r),
      "ThisValue" -> ESValue,
      "ThisBindingStatus" -> getConsts("lexical", "initialized", "uninitialized"),
      "FunctionObject" -> AbsTy("OrdinaryObject"),
      "NewTarget" -> AbsValue(Ty("OrdinaryObject")) ⊔ AbsValue(Undef),
      "OuterEnv" -> AbsValue(Ty("EnvironmentRecord")) ⊔ AbsValue(Null),
      "ObjectRecord" -> AbsTy("EnvironmentRecord"),
      "GlobalThisValue" -> AbsValue(NamedAddr("Global")),
      "DeclarativeRecord" -> AbsTy("EnvironmentRecord"),
      "VarNames" -> AbsValue(NamedAddr("StringList")),
      "HasVarDeclaration" -> getClos(""".*\.HasVarDeclaration""".r),
      "HasLexicalDeclaration" -> getClos(""".*\.HasLexicalDeclaration""".r),
      "HasRestrictedGlobalProperty" -> getClos(""".*\.HasRestrictedGlobalProperty""".r),
      "CanDeclareGlobalVar" -> getClos(""".*\.CanDeclareGlobalVar""".r),
      "CanDeclareGlobalFunction" -> getClos(""".*\.CanDeclareGlobalFunction""".r),
      "CreateGlobalVarBinding" -> getClos(""".*\.CreateGlobalVarBinding""".r),
      "CreateGlobalFunctionBinding" -> getClos(""".*\.CreateGlobalFunctionBinding""".r),
      "CreateImportBinding" -> getClos(""".*\.CreateImportBinding""".r),
      "BindingObject" -> AbsTy("OrdinaryObject"),
      "SubMap" -> AbsValue(Ty("SubMap")),
    ),
    TyInfo(
      name = "DeclarativeEnvironmentRecord",
      parent = "EnvironmentRecord",
      "CreateImmutableBinding" -> getClos("""DeclarativeEnvironmentRecord.CreateImmutableBinding""".r),
    ),
    // objects
    TyInfo(
      name = "Object",
      "SubMap" -> AbsValue(Ty("SubMap")),
      "Prototype" -> AbsValue(Ty("Object")) ⊔ AbsNull.Top,
      "Extensible" -> AbsBool.Top,
      "GetPrototypeOf" -> getClos("""OrdinaryObject.GetPrototypeOf""".r),
      "SetPrototypeOf" -> getClos("""OrdinaryObject.SetPrototypeOf""".r),
      "IsExtensible" -> getClos("""OrdinaryObject.IsExtensible""".r),
      "PreventExtensions" -> getClos("""OrdinaryObject.PreventExtensions""".r),
      "GetOwnProperty" -> getClos("""OrdinaryObject.GetOwnProperty""".r),
      "DefineOwnProperty" -> getClos("""OrdinaryObject.DefineOwnProperty""".r),
      "HasProperty" -> getClos("""OrdinaryObject.HasProperty""".r),
      "Get" -> getClos("""OrdinaryObject.Get""".r),
      "Set" -> getClos("""OrdinaryObject.Set""".r),
      "Delete" -> getClos("""OrdinaryObject.Delete""".r),
      "OwnPropertyKeys" -> getClos("""OrdinaryObject.OwnPropertyKeys""".r),
      "InitialName" -> AbsValue(Null, Absent) ⊔ AbsStr.Top
    ),
    TyInfo(
      name = "OrdinaryObject",
      parent = "Object",
    ),
    TyInfo(
      name = "ECMAScriptFunctionObject",
      // internal slots
      "Environment" -> AbsValue(Ty("EnvironmentRecord")),
      "FormalParameters" -> AbsValue(ASTVal("FormalParameters")),
      "ECMAScriptCode" -> AbsValue(ASTVal("AnyNode")),
      "ConstructorKind" -> getConsts("base", "derived"),
      "Realm" -> AbsValue(Ty("RealmRecord")),
      "ScriptOrModule" -> AbsTy(Ty("ScriptRecord"), Ty("ModuleRecord")),
      "ThisMode" -> getConsts("lexical", "strict", "global"),
      "Strict" -> AbsBool.Top,
      "HomeObject" -> AbsValue(Ty("Object")),
      "SourceText" -> AbsStr.Top,
      "IsClassConstructor" -> AbsBool.Top,
      // call & construct
      "Call" -> getClos("""ECMAScriptFunctionObject.Call""".r),
      "Construct" -> getClos("""ECMAScriptFunctionObject.Construct""".r),
      // prototype, extensible
      "Prototype" -> AbsValue(Ty("Object")) ⊔ AbsNull.Top,
      "Extensible" -> AbsBool.Top,
      // ordinary object method
      "GetPrototypeOf" -> getClos("""OrdinaryObject.GetPrototypeOf""".r),
      "SetPrototypeOf" -> getClos("""OrdinaryObject.SetPrototypeOf""".r),
      "IsExtensible" -> getClos("""OrdinaryObject.IsExtensible""".r),
      "PreventExtensions" -> getClos("""OrdinaryObject.PreventExtensions""".r),
      "GetOwnProperty" -> getClos("""OrdinaryObject.GetOwnProperty""".r),
      "DefineOwnProperty" -> getClos("""OrdinaryObject.DefineOwnProperty""".r),
      "HasProperty" -> getClos("""OrdinaryObject.HasProperty""".r),
      "Get" -> getClos("""OrdinaryObject.Get""".r),
      "Set" -> getClos("""OrdinaryObject.Set""".r),
      "Delete" -> getClos("""OrdinaryObject.Delete""".r),
      "OwnPropertyKeys" -> getClos("""OrdinaryObject.OwnPropertyKeys""".r),
    ),
    TyInfo(
      name = "BuiltinFunctionObject",
      // internal slots
      "InitialName" -> AbsStr.Top ⊔ AbsValue(Null),
      // call & construct
      "Call" -> getClos("""BuiltinFunctionObject.Call""".r),
      "Construct" -> getClos("""BuiltinFunctionObject.Construct""".r),
      // prototype, extensible, realm
      "Prototype" -> AbsValue(Ty("Object")) ⊔ AbsNull.Top,
      "Extensible" -> AbsBool.Top,
      "Realm" -> AbsValue(Ty("RealmRecord")),
      // ordinary object method
      "GetPrototypeOf" -> getClos("""OrdinaryObject.GetPrototypeOf""".r),
      "SetPrototypeOf" -> getClos("""OrdinaryObject.SetPrototypeOf""".r),
      "IsExtensible" -> getClos("""OrdinaryObject.IsExtensible""".r),
      "PreventExtensions" -> getClos("""OrdinaryObject.PreventExtensions""".r),
      "GetOwnProperty" -> getClos("""OrdinaryObject.GetOwnProperty""".r),
      "DefineOwnProperty" -> getClos("""OrdinaryObject.DefineOwnProperty""".r),
      "HasProperty" -> getClos("""OrdinaryObject.HasProperty""".r),
      "Get" -> getClos("""OrdinaryObject.Get""".r),
      "Set" -> getClos("""OrdinaryObject.Set""".r),
      "Delete" -> getClos("""OrdinaryObject.Delete""".r),
      "OwnPropertyKeys" -> getClos("""OrdinaryObject.OwnPropertyKeys""".r),
    ),
    TyInfo(
      name = "StringExoticObject",
      // TODO add "length" property to SubMap
      // GetOwnProperty && DefineOwnProperty && OwnPropertyKeys
      "GetOwnProperty" -> getClos("""StringExoticObject.GetOwnProperty""".r),
      "DefineOwnProperty" -> getClos("""StringExoticObject.DefineOwnProperty""".r),
      "OwnPropertyKeys" -> getClos("""StringExoticObject.OwnPropertyKeys""".r),
      // ordinary object method
      "GetPrototypeOf" -> getClos("""OrdinaryObject.GetPrototypeOf""".r),
      "SetPrototypeOf" -> getClos("""OrdinaryObject.SetPrototypeOf""".r),
      "IsExtensible" -> getClos("""OrdinaryObject.IsExtensible""".r),
      "PreventExtensions" -> getClos("""OrdinaryObject.PreventExtensions""".r),
      "HasProperty" -> getClos("""OrdinaryObject.HasProperty""".r),
      "Get" -> getClos("""OrdinaryObject.Get""".r),
      "Set" -> getClos("""OrdinaryObject.Set""".r),
      "Delete" -> getClos("""OrdinaryObject.Delete""".r),
    ),
    // property descriptor
    TyInfo(
      name = "PropertyDescriptor",
      "Value" -> ESValue ⊔ AbsAbsent.Top,
      "Writable" -> AbsValue(true, false, Absent),
      "Get" -> AbsValue(Ty("Object")),
      "Set" -> AbsValue(Ty("Object")),
      "Enumerable" -> AbsBool.Top,
      "Configurable" -> AbsBool.Top,
    ),
    // reference record
    TyInfo(
      name = "ReferenceRecord",
      "Value" -> (ESValue ⊓ AbsUndef.Bot ⊓ AbsNull.Bot) ⊔ AbsValue(Ty("EnvironmentRecord")) ⊔ getConsts("unresolvable"),
      "ReferencedName" -> AbsStr.Top,
      "Strict" -> AbsBool.Top,
      "ThisValue" -> ESValue ⊔ getConsts("emtpy"),
    )
  )

  // TODO more manual modelings
  private def manualEnv: Map[String, AbsValue] = Map(
    "GLOBAL_context" -> AbsValue(Ty("ExecutionContext")),
    "GLOBAL_executionStack" -> AbsValue(NamedAddr("ExecutionStack")),
    "REALM" -> AbsValue(Ty("RealmRecord")),
    "Object" -> AbsValue("Object"),
    "String" -> AbsValue("String"),
    "Symbol" -> AbsValue("Symbol"),
    "Undefined" -> AbsValue("Undefined"),
    "Null" -> AbsValue("Null"),
    "Boolean" -> AbsValue("Boolean"),
    "Number" -> AbsValue("Number"),
    "BigInt" -> AbsValue("BigInt"),
    "PRIMITIVE" -> AbsValue(NamedAddr("PRIMITIVE")),
    "AnyStr" -> AbsStr.Top,
    "AnyBool" -> AbsBool.Top,
    "AnyNum" -> AbsNum.Top,
    "FunctionBody" -> AbsValue(ASTVal("FunctionBody")),
    "ConciseBody" -> AbsValue(ASTVal("ConciseBody")),
    "AsyncConciseBody" -> AbsValue(ASTVal("AsyncConciseBody")),
  )

  // TODO more manual modelings
  private def manualMaps: Map[String, (Option[String], Map[String, AbsValue])] = Map(
    "Global" -> (Some("OrdinaryObject"), Map()),
    "Intrinsics" -> (None, Map(
      // TODO automatic insertion from specification
      "%String.prototype%" -> AbsValue(NamedAddr("%String.prototype%")),
      "%RegExp.prototype%" -> AbsValue(NamedAddr("%RegExp.prototype%")),
    )),
    // primitive object for numeric values
    "PRIMITIVE" -> (None, Map(
      "Number" -> AbsValue(NamedAddr("PRIMITIVE.Number")),
      "BigInt" -> AbsValue(NamedAddr("PRIMITIVE.BigInt")),
    )),
    "PRIMITIVE.Number" -> (None, Map(
      "unit" -> AbsValue(Num(1)),
      "unaryMinus" -> getClo("Number::unaryMinus"),
      "bitwiseNOT" -> getClo("Number::bitwiseNOT"),
      "exponentiate" -> getClo("Number::exponentiate"),
      "multiply" -> getClo("Number::multiply"), // XXX
      "divide" -> getClo("Number::divide"), // XXX
      "remainder" -> getClo("Number::remainder"), // XXX
      "add" -> getClo("Number::add"), // XXX
      "subtract" -> getClo("Number::subtract"),
      "leftShift" -> getClo("Number::leftShift"),
      "signedRightShift" -> getClo("Number::signedRightShift"),
      "unsignedRightShift" -> getClo("Number::unsignedRightShift"),
      "lessThan" -> getClo("Number::lessThan"),
      "equal" -> getClo("Number::equal"),
      "sameValue" -> getClo("Number::sameValue"),
      "sameValueZero" -> getClo("Number::sameValueZero"),
      "bitwiseAND" -> getClo("Number::bitwiseAND"),
      "bitwiseXOR" -> getClo("Number::bitwiseXOR"),
      "bitwiseOR" -> getClo("Number::bitwiseOR"),
      "toString" -> getClo("Number::toString"),
    )),
    "PRIMITIVE.BigInt" -> (None, Map(
      "unit" -> AbsValue(BigInt(1)),
      "unaryMinus" -> getClo("BigInt::unaryMinus"),
      "bitwiseNOT" -> getClo("BigInt::bitwiseNOT"), // XXX
      "exponentiate" -> getClo("BigInt::exponentiate"),
      "multiply" -> getClo("BigInt::multiply"), // XXX
      "divide" -> getClo("BigInt::divide"),
      "remainder" -> getClo("BigInt::remainder"),
      "add" -> getClo("BigInt::add"), // XXX
      "subtract" -> getClo("BigInt::subtract"), // XXX
      "leftShift" -> getClo("BigInt::leftShift"),
      "signedRightShift" -> getClo("BigInt::signedRightShift"),
      "unsignedRightShift" -> getClo("BigInt::unsignedRightShift"),
      "lessThan" -> getClo("BigInt::lessThan"), // XXX
      "equal" -> getClo("BigInt::equal"), // XXX
      "sameValue" -> getClo("BigInt::sameValue"),
      "sameValueZero" -> getClo("BigInt::sameValueZero"),
      "bitwiseAND" -> getClo("BigInt::bitwiseAND"),
      "bitwiseXOR" -> getClo("BigInt::bitwiseXOR"),
      "bitwiseOR" -> getClo("BigInt::bitwiseOR"),
      "toString" -> getClo("BigInt::toString"),
    )),
    // built-in objects
    "%AsyncFunction%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("Function")),
      "Call" -> getClo("AsyncFunction"),
      "Construct" -> getClo("AsyncFunction"),
    )),
    "%AsyncFunction.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%"))
    )),
    "%Function%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
      "Call" -> getClo("Function"),
      "Construct" -> getClo("Function"),
    )),
    "%Function.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
      "Call" -> AbsClo.Bot // TODO accepts any arguments and returns undefined when invoked. (See 19.2.3 Properties of the Function Prototype Object)
    )),
    "%AsyncGeneratorFunction%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function%")),
      "Call" -> getClo("AsyncGeneratorFunction"),
      "Construct" -> getClo("AsyncGeneratorFunction"),
    )),
    "%AsyncGeneratorFunction.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
    )),
    "%Object%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
      "Call" -> getClo("Object"),
      "Construct" -> getClo("Object"),
    )),
    "%Object.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsNull.Top,
      "Extensible" -> AT,
    )),
    "%Promise%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
      "Call" -> getClo("Promise"),
      "Construct" -> getClo("Promise"),
    )),
    "%Promise.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
    )),
    "%Array%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
      "Call" -> getClo("Array"),
      "Construct" -> getClo("Array"),
    )),
    "%Array.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
    )),
    "%AggregateError%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Error%")),
      "Call" -> getClo("AggregateError"),
      "Construct" -> getClo("AggregateError"),
    )),
    "%AggregateError.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Error.prototype%")),
    )),
    "%ArrayBuffer%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
      "Call" -> getClo("ArrayBuffer"),
      "Construct" -> getClo("ArrayBuffer"),
    )),
    "%ArrayBuffer.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
    )),
    "%ArrayIteratorPrototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%IteratorPrototype%")),
    )),
    "%AsyncFromSyncIteratorPrototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%AsyncIteratorPrototype%")),
    )),
    "%IteratorPrototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
    )),
    "%AsyncIteratorPrototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
    )),
    "%Atomics%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
    )),
    "%BigInt%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
      "Call" -> getClo("BigInt"),
      "Construct" -> getClo("BigInt"),
    )),
    "%BigInt.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
    )),
    "%Boolean%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
      "Call" -> getClo("Boolean"),
      "Construct" -> getClo("Boolean"),
    )),
    "%Boolean.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
      "BooleanData" -> AF,
    )),
    "%DataView%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("Function.prototype")),
      "Call" -> getClo("DataView"),
      "Construct" -> getClo("DataView"),
    )),
    "%DataView.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
    )),
    "%GeneratorFunction%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function%")),
      "Call" -> getClo("GeneratorFunction"),
      "Construct" -> getClo("GeneratorFunction"),
    )),
    "%GeneratorFunction.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
    )),
    "%Error%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
      "Call" -> getClo("Error"),
      "Construct" -> getClo("Error"),
    )),
    "%Error.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
    )),
    "%EvalError%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Error%")),
      "Call" -> getClo("EvalError"),
      "Construct" -> getClo("EvalError"),
    )),
    "%EvalError.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Error.prototype%")),
    )),
    "%RangeError%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Error%")),
      "Call" -> getClo("RangeError"),
      "Construct" -> getClo("RangeError"),
    )),
    "%RangeError.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Error.prototype%")),
    )),
    "%ReferenceError%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Error%")),
      "Call" -> getClo("ReferenceError"),
      "Construct" -> getClo("ReferenceError"),
    )),
    "%ReferenceError.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Error.prototype%")),
    )),
    "%SyntaxError%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Error%")),
      "Call" -> getClo("SyntaxError"),
      "Construct" -> getClo("SyntaxError"),
    )),
    "%SyntaxError.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Error.prototype%")),
    )),
    "%TypeError%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Error%")),
      "Call" -> getClo("TypeError"),
      "Construct" -> getClo("TypeError"),
    )),
    "%TypeError.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Error.prototype%")),
    )),
    "%URIError%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Error%")),
      "Call" -> getClo("URIError"),
      "Construct" -> getClo("URIError"),
    )),
    "%URIError.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Error.prototype%")),
    )),
    "%Date%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
      "Call" -> getClo("Date"),
      "Construct" -> getClo("Date"),
    )),
    "%Date.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
    )),
    "%eval%" -> (Some("OrdinaryObject"), Map(
      "Call" -> getClo("eval"),
    )),
    "%FinalizationRegistry%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
      "Call" -> getClo("FinalizationRegistry"),
      "Construct" -> getClo("FinalizationRegistry"),
    )),
    "%FinalizationRegistry.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
    )),
    "%ForInIteratorPrototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%IteratorPrototype%")),
    )),
    "%isFinite%" -> (Some("OrdinaryObject"), Map(
      "Call" -> getClo("isFinite"),
    )),
    "%isNaN%" -> (Some("OrdinaryObject"), Map(
      "Call" -> getClo("isNaN"),
    )),
    "%RegExp%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
      "Call" -> getClo("RegExp"),
      "Construct" -> getClo("RegExp"),
    )),
    "%RegExp.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
    )),
    "%GeneratorFunction.prototype.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%IteratorPrototype%")),
    // TODO ??? has properties that are indirectly inherited by all Generator instances.
    )),
    "%AsyncGeneratorFunction.prototype.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%AsyncIteratorPrototype%")),
    // TODO ??? has properties that are indirectly inherited by all AsyncGenerator instances.
    )),
    "%AsyncGeneratorFunction.prototype.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%AsyncIteratorPrototype%")),
    // TODO ??? has properties that are indirectly inherited by all AsyncGenerator instances.
    )),
    "%Number%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
      "Call" -> getClo("Number"),
      "Construct" -> getClo("Number"),
    )),
    "%Number.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
      "NumberData" -> AbsValue(0),
    )),
    "%String%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
      "Call" -> getClo("String"),
      "Construct" -> getClo("String"),
    )),
    "%String.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
      "StringData" -> AbsValue(""),
    )),
    "%Symbol%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Function.prototype%")),
      "Call" -> getClo("Symbol"),
      "Construct" -> getClo("Symbol"),
    )),
    "%Symbol.prototype%" -> (Some("OrdinaryObject"), Map(
      "Prototype" -> AbsValue(NamedAddr("%Object.prototype%")),
    )),
  // TODO model following remaining intrinsics (ommitted some cases that seems unnecessary)
  // %JSON% %Map% %MapIteratorPrototype% %Math% %parseFloat% %parseInt% %Promise% %Proxy% %Reflect% %RegExpStringIteratorPrototype% %Set% %SetIteratorPrototype% %StringIteratorPrototype% %TypedArray% %Uint8Array% %Uint8ClampedArray% %Uint16Array% %Uint32Array%
  )

  // TODO more manual modelings
  private def manualLists: Map[String, AbsValue] = Map(
    "ExecutionStack" -> AbsValue(Ty("ExecutionContext")),
    "StringList" -> AbsStr.Top,
    "ImportEntries" -> AbsValue(Ty("ImportEntryRecord")),
    "ExportEntries" -> AbsValue(Ty("ExportEntryRecord")),
    "TemplateMap" -> AbsValue(Ty("TemplatePair")),
  )

  private def getClos(pattern: Regex): AbsValue = AbsValue(for {
    func <- cfg.funcs.toSet
    if pattern.matches(func.name)
  } yield (Clo(func.uid, Env()): Value))
  private lazy val cloMap: Map[String, AbsValue] =
    (for (func <- cfg.funcs) yield func.name -> AbsValue(Clo(func.uid))).toMap
  private def getClo(name: String): AbsValue = cloMap.getOrElse(name, {
    alarm(s"unknown function name: $name")
    AbsValue.Bot
  })
  private def getConsts(names: String*): AbsValue =
    AbsValue(names.toSet.map[Value](Const(_)))
}
