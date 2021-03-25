package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.ir.doubleEquals
import kr.ac.kaist.jiset.util.Useful._
import scala.annotation.tailrec

sealed trait Type {
  import Type._

  // conversion to abstract type
  def abs: AbsType = Type.abs(this)

  // get root of type
  def root: Type = parent.fold(this)(_.root)

  // get ancestor types
  def ancestors: Set[Type] = parent.map(_.ancestors).getOrElse(Set()) + this
  def strictAncestors: Set[Type] = parent.map(_.ancestors).getOrElse(Set())

  // check sub typing
  @tailrec
  final def <(that: Type): Boolean = {
    if (this == that) true
    else parent match {
      case Some(parent) => parent < that
      case None => false
    }
  }

  // remove types
  def -(that: Type): Set[Type] =
    if (this < that) Set() else typeAliasMap.get(this) match {
      case Some(set) if set contains that => set - that
      case _ => Set(this)
    }

  // get base types
  def bases: Set[Type] = baseMap.getOrElse(this, Set(this))

  // get type names
  def typeNameSet: Set[String] = for {
    x <- bases
    y <- x.typeName
  } yield y

  // get instance name
  def instanceName: Option[String] = optional(this match {
    case AstT(name) => name
    case NameT(name) => name
    case _ => error("no instance name")
  })

  // get name of base types
  def typeName: Option[String] = optional(this match {
    case NameT(name) if name endsWith "Object" => "Object"
    case NameT("ReferenceRecord") => "Reference"
    case SymbolT => "Symbol"
    case NumT | Num(_) => "Number"
    case BigIntT | BigInt(_) => "BigInt"
    case StrT | Str(_) => "String"
    case BoolT | Bool(_) => "Boolean"
    case Undef => "Undefined"
    case Null => "Null"
  })

  // get parent types
  def parent: Option[Type] = optional(this match {
    case NormalT(t) => t.parent match {
      case Some(parent: PureType) => NormalT(parent)
      case _ => error("no parent")
    }
    case NameT("Object") => ESValueT
    case NameT(name) => infoMap.get(name) match {
      case Some(Info(_, Some(parent), _)) => NameT(parent)
      case _ => error("no parent")
    }
    case PrimT => ESValueT
    case ArithT => PrimT
    case NumericT => ArithT
    case NumT => NumericT
    case BigIntT => NumericT
    case StrT => ArithT
    case BoolT => PrimT
    case SymbolT => PrimT
    case Num(n) => NumT
    case BigInt(b) => BigIntT
    case Str(str) => StrT
    case Bool(b) => BoolT
    case Undef => PrimT
    case Null => PrimT
    case _ => error("no parent")
  })

  // conversion to completions
  def toComp: CompType = this match {
    case (t: PureType) => NormalT(t)
    case (t: CompType) => t
  }

  // escape completions
  def escaped: Option[PureType] = this match {
    case (t: PureType) => Some(t)
    case NormalT(t) => Some(t)
    case AbruptT =>
      alarm(s"Unchecked abrupt completions")
      None
  }

  // upcast
  def upcast: Type = this match {
    case NormalT(t) => NormalT(t.upcast)
    case p: PureType => p.upcast
    case _ => this
  }

  // conversion to string
  override def toString: String = this match {
    case NameT(name) => s"$name"
    case AstT(name) => s"☊($name)"
    case ConstT(name) => s"~$name~"
    case CloT(fid) => s"λ[$fid]"
    case ESValueT => s"ESValue"
    case PrimT => "prim"
    case ArithT => "arith"
    case NumericT => "numeric"
    case NumT => "num"
    case BigIntT => "bigint"
    case StrT => "str"
    case BoolT => "bool"
    case NilT => s"[]"
    case ListT(elem) => s"[$elem]"
    case MapT(elem) => s"{ _ |-> $elem }"
    case SymbolT => "symbol"
    case NormalT(t) => s"Normal($t)"
    case AbruptT => s"Abrupt"
    case Num(n) => s"$n"
    case BigInt(b) => s"${b}n"
    case Str(str) => "\"" + str + "\""
    case Bool(b) => s"$b"
    case Undef => "undef"
    case Null => "null"
    case Absent => "?"
  }
}

// completion types
sealed trait CompType extends Type
case class NormalT(value: PureType) extends CompType
case object AbruptT extends CompType

// pure types
sealed trait PureType extends Type {
  // upcast
  override def upcast: PureType = this match {
    case ListT(t) => ListT(t.upcast)
    case MapT(t) => ListT(t.upcast)
    case Num(_) => NumT
    case BigInt(_) => BigIntT
    case Str(_) => StrT
    case Bool(_) => BoolT
    case _ => this
  }
}

// ECMAScript value types
case object ESValueT extends PureType

// norminal types
case class NameT(name: String) extends PureType {
  // lookup propertys
  def apply(prop: String): AbsType =
    Type.propMap.getOrElse(name, Map()).getOrElse(prop, Absent)
}

// AST types
case class AstT(name: String) extends PureType

// constant types
case class ConstT(name: String) extends PureType

// closure types
case class CloT(fid: Int) extends PureType

// list types
case object NilT extends PureType
case class ListT(elem: PureType) extends PureType

// sub mapping types
case class MapT(elem: PureType) extends PureType

// symbol types
case object SymbolT extends PureType

// primitive types
case object PrimT extends PureType
case object ArithT extends PureType
case object NumericT extends PureType
case object NumT extends PureType
case object BigIntT extends PureType
case object StrT extends PureType
case object BoolT extends PureType

// single concrete type
sealed trait SingleT extends PureType
case class Num(double: Double) extends SingleT {
  override def equals(that: Any): Boolean = that match {
    case that: Num => doubleEquals(this.double, that.double)
    case _ => false
  }
}
case class BigInt(bigint: scala.BigInt) extends SingleT
case class Str(str: String) extends SingleT
case class Bool(bool: Boolean) extends SingleT
case object Undef extends SingleT
case object Null extends SingleT
case object Absent extends SingleT

// modeling
object Type {
  // type aliases
  val typeAlias: List[(Type, Set[Type])] = List(
    BoolT -> Set[Type](Bool(true), Bool(false)),
    NumericT -> Set[Type](NumT, BigIntT),
    ArithT -> Set[Type](NumericT, StrT),
    PrimT -> Set[Type](Null, Undef, BoolT, ArithT, SymbolT),
    ESValueT -> Set[Type](NameT("Object"), PrimT),
  )
  val typeAliasMap: Map[Type, Set[Type]] = typeAlias.toMap
  val baseMap: Map[Type, Set[Type]] = {
    var map = Map[Type, Set[Type]]()
    for ((t, set) <- typeAlias) map += t -> set.flatMap(x => {
      map.get(x).getOrElse(Set(x))
    })
    map
  }

  // abstraction
  val abs: Type => AbsType = cached(AbsType(_))

  //////////////////////////////////////////////////////////////////////////////
  // Type Information
  //////////////////////////////////////////////////////////////////////////////
  case class Info(
    name: String,
    parent: Option[String],
    lazyProps: () => Map[String, AbsType]
  ) { lazy val props: Map[String, AbsType] = lazyProps() }

  // constructors
  def I(name: String, parent: String, props: => Map[String, AbsType]): Info =
    Info(name, Some(parent), () => props)
  def I(name: String, props: => Map[String, AbsType]): Info =
    Info(name, None, () => props)

  // property map
  type PropMap = Map[String, AbsType]

  // get type information
  lazy val infos: List[Info] = getInfos

  // type info map
  lazy val infoMap: Map[String, Info] =
    infos.map(info => info.name -> info).toMap

  // sub types
  lazy val subTypes: Map[String, Set[String]] = {
    var children = Map[String, Set[String]]()
    for {
      info <- infos
      parent <- info.parent
      set = children.getOrElse(parent, Set())
    } children += parent -> (set + info.name)
    children
  }

  // property map
  lazy val propMap: Map[String, PropMap] =
    infos.map(info => info.name -> getPropMap(info.name)).toMap

  //////////////////////////////////////////////////////////////////////////////
  // Private Helper Functions
  //////////////////////////////////////////////////////////////////////////////
  // get property map
  private def getPropMap(name: String): PropMap = {
    val upper = getUpperPropMap(name)
    val lower = getLowerPropMap(name)
    lower.foldLeft(upper) {
      case (map, (k, t)) =>
        val newT = t ⊔ map.getOrElse(k, AbsType.Bot)
        map + (k -> newT)
    }
  }

  // get property map from ancestors
  private def getUpperPropMap(name: String): PropMap = infoMap.get(name) match {
    case Some(info) => info.parent.map(getUpperPropMap).getOrElse(Map()) ++ info.props
    case None => Map()
  }

  // get property map from ancestors
  private def getLowerPropMap(name: String): PropMap = subTypes
    .getOrElse(name, Set())
    .map(child => {
      val lower = getLowerPropMap(child)
      val props = infoMap.get(child).map(_.props).getOrElse(Map())
      lower ++ props
    })
    .reduceOption(weakMerge)
    .getOrElse(Map())

  // weak merge
  private def weakMerge(lmap: PropMap, rmap: PropMap): PropMap = {
    val keys = lmap.keySet ++ rmap.keySet
    keys.toList.map(k => {
      k -> (lmap.getOrElse(k, Absent.abs) ⊔ rmap.getOrElse(k, Absent.abs))
    }).toMap
  }

  // get function closure by name
  private lazy val cloMap: Map[String, AbsType] =
    (for (func <- cfg.funcs) yield func.name -> CloT(func.uid).abs).toMap
  private def getClo(name: String): AbsType = cloMap.getOrElse(name, {
    warning(s"unknown function name: $name")
    Absent
  })

  // get all type info
  // TODO extract from specification
  private def getInfos: List[Info] = List(
    // realm records
    I("RealmRecord", Map(
      "Intrinsics" -> MapT(NameT("OrdinaryObject")),
      "GlobalObject" -> NameT("OrdinaryObject"),
      "GlobalEnv" -> NameT("GlobalEnvironmentRecord"),
      "TemplateMap" -> ListT(NameT("TemplatePair")),
      "HostDefined" -> Undef,
    )),
    I("TemplatePair", Map(
      "Site" -> AstT("TemplateLiteral"),
      "Array" -> NameT("Object"),
    )),

    // property descriptors
    I("PropertyDescriptor", Map(
      "Value" -> AbsType(ESValueT, Absent),
      "Writable" -> AbsType(BoolT, Absent),
      "Get" -> AbsType(NameT("FunctionObject"), Undef, Absent),
      "Set" -> AbsType(NameT("FunctionObject"), Undef, Absent),
      "Enumerable" -> AbsType(BoolT, Absent),
      "Configurable" -> AbsType(BoolT, Absent),
    )),

    // objects
    I("Object", Map(
      "SubMap" -> MapT(NameT("PropertyDescriptor")),
      "Prototype" -> AbsType(NameT("Object"), Null),
      "Extensible" -> BoolT,
      "GetPrototypeOf" -> getClo("OrdinaryObject.GetPrototypeOf"),
      "SetPrototypeOf" -> getClo("OrdinaryObject.SetPrototypeOf"),
      "IsExtensible" -> getClo("OrdinaryObject.IsExtensible"),
      "PreventExtensions" -> getClo("OrdinaryObject.PreventExtensions"),
      "GetOwnProperty" -> getClo("OrdinaryObject.GetOwnProperty"),
      "DefineOwnProperty" -> getClo("OrdinaryObject.DefineOwnProperty"),
      "HasProperty" -> getClo("OrdinaryObject.HasProperty"),
      "Get" -> getClo("OrdinaryObject.Get"),
      "Set" -> getClo("OrdinaryObject.Set"),
      "Delete" -> getClo("OrdinaryObject.Delete"),
      "OwnPropertyKeys" -> getClo("OrdinaryObject.OwnPropertyKeys"),
    )),
    I("OrdinaryObject", parent = "Object", Map()),
    I("FunctionObject", parent = "OrdinaryObject", Map()),
    I("ECMAScriptFunctionObject", parent = "FunctionObject", Map(
      "Environment" -> NameT("EnvironmentRecord"),
      "FormalParameters" -> AstT("FormalParameters"),
      "ECMAScriptCode" -> AstT("FunctionBody"),
      "ConstructorKind" -> AbsType(BASE, DERIVED),
      "Realm" -> NameT("RealmRecord"),
      "ScriptOrModule" -> AbsType(NameT("ScriptRecord"), NameT("ModuleRecord")),
      "ThisMode" -> AbsType(LEXICAL, STRICT, GLOBAL),
      "Strict" -> BoolT,
      "HomeObject" -> NameT("Object"),
      "SourceText" -> StrT,
      "IsClassConstructor" -> BoolT,
      "Call" -> getClo("ECMAScriptFunctionObject.Call"),
      "Construct" -> getClo("ECMAScriptFunctionObject.Construct"),
    )),
    I("BuiltinFunctionObject", parent = "FunctionObject", Map(
      "InitialName" -> AbsType(StrT, Null),
      "Call" -> getClo("BuiltinFunctionObject.Call"),
      "Construct" -> getClo("BuiltinFunctionObject.Construct"),
      "Realm" -> NameT("RealmRecord"),
    )),
    I("BoundFunctionExoticObject", parent = "Object", Map(
      "BoundTargetFunction" -> NameT("FunctionObject"),
      "BoundThis" -> ESValueT,
      "BoundArguments" -> ListT(ESValueT),
      "Call" -> getClo("BoundFunctionExoticObject.Call"),
      "Construct" -> getClo("BoundFunctionExoticObject.Construct"),
    )),
    I("ArrayExoticObject", parent = "Object", Map(
      "DefineOwnProperty" -> getClo("ArrayExoticObject.DefineOwnProperty"),
    )),
    I("StringExoticObject", parent = "Object", Map(
      "GetOwnProperty" -> getClo("StringExoticObject.GetOwnProperty"),
      "DefineOwnProperty" -> getClo("StringExoticObject.DefineOwnProperty"),
      "OwnPropertyKeys" -> getClo("StringExoticObject.OwnPropertyKeys"),
    )),
    I("ArgumentsExoticObject", parent = "Object", Map(
      "ParameterMap" -> AbsType(NameT("OrdinaryObject"), Undef),
      "GetOwnProperty" -> getClo("ArgumentsExoticObject.GetOwnProperty"),
      "DefineOwnProperty" -> getClo("ArgumentsExoticObject.DefineOwnProperty"),
      "Get" -> getClo("ArgumentsExoticObject.Get"),
      "Set" -> getClo("ArgumentsExoticObject.Set"),
      "Delete" -> getClo("ArgumentsExoticObject.Delete"),
    )),
    I("IntegerIndexedExoticObject", parent = "Object", Map(
      "ViewedArrayBuffer" -> NameT("ArrayBufferObject"),
      "ArrayLength" -> NumT,
      "ByteOffset" -> NumT,
      "ContentType" -> AbsType(NUMBER, BIGINT),
      "TypedArrayName" -> StrT,
      "GetOwnProperty" -> getClo("IntegerIndexedExoticObject.GetOwnProperty"),
      "HasProperty" -> getClo("IntegerIndexedExoticObject.HasProperty"),
      "DefineOwnProperty" -> getClo("IntegerIndexedExoticObject.DefineOwnProperty"),
      "Get" -> getClo("IntegerIndexedExoticObject.Get"),
      "Set" -> getClo("IntegerIndexedExoticObject.Set"),
      "Delete" -> getClo("IntegerIndexedExoticObject.Delete"),
      "OwnPropertyKeys" -> getClo("IntegerIndexedExoticObject.OwnPropertyKeys"),
    )),
    I("ModuleNamespaceExoticObject", parent = "Object", Map(
      "Module" -> NameT("ModuleRecord"),
      "Exports" -> ListT(StrT),
      "Prototype" -> Null,
      "SetPrototypeOf" -> getClo("ModuleNamespaceExoticObject.SetPrototypeOf"),
      "IsExtensible" -> getClo("ModuleNamespaceExoticObject.IsExtensible"),
      "PreventExtensions" -> getClo("ModuleNamespaceExoticObject.PreventExtensions"),
      "GetOwnProperty" -> getClo("ModuleNamespaceExoticObject.GetOwnProperty"),
      "DefineOwnProperty" -> getClo("ModuleNamespaceExoticObject.DefineOwnProperty"),
      "HasProperty" -> getClo("ModuleNamespaceExoticObject.HasProperty"),
      "Get" -> getClo("ModuleNamespaceExoticObject.Get"),
      "Set" -> getClo("ModuleNamespaceExoticObject.Set"),
      "Delete" -> getClo("ModuleNamespaceExoticObject.Delete"),
      "OwnPropertyKeys" -> getClo("ModuleNamespaceExoticObject.OwnPropertyKeys"),
    )),
    I("ImmutablePrototypeExoticObject", parent = "Object", Map(
      "SetPrototypeOf" -> getClo("ImmutablePrototypeExoticObject.SetPrototypeOf"),
    )),
    I("ProxyObject", parent = "Object", Map(
      "ProxyHandler" -> AbsType(NameT("Object"), Null),
      "ProxyTarget" -> AbsType(NameT("Object"), Null),
      "GetPrototypeOf" -> getClo("ProxyObject.GetPrototypeOf"),
      "SetPrototypeOf" -> getClo("ProxyObject.SetPrototypeOf"),
      "IsExtensible" -> getClo("ProxyObject.IsExtensible"),
      "PreventExtensions" -> getClo("ProxyObject.PreventExtensions"),
      "GetOwnProperty" -> getClo("ProxyObject.GetOwnProperty"),
      "DefineOwnProperty" -> getClo("ProxyObject.DefineOwnProperty"),
      "HasProperty" -> getClo("ProxyObject.HasProperty"),
      "Get" -> getClo("ProxyObject.Get"),
      "Set" -> getClo("ProxyObject.Set"),
      "Delete" -> getClo("ProxyObject.Delete"),
      "OwnPropertyKeys" -> getClo("ProxyObject.OwnPropertyKeys"),
      "Call" -> getClo("ProxyObject.Call"),
      "Construct" -> getClo("ProxyObject.Construct"),
    )),
    I("ArrayBufferObject", parent = "Object", Map(
      "ArrayBufferData" -> AbsType(NameT("DataBlock"), Null),
      "ArrayBufferByteLength" -> NumT,
      "ArrayBufferDetachKey" -> Undef,
    )),

    // reference records
    I("ReferenceRecord", Map(
      "Base" -> AbsType(ESValueT, NameT("EnvironmentRecord"), UNRESOLVABLE),
      "ReferencedName" -> AbsType(StrT, SymbolT),
      "Strict" -> BoolT,
      "ThisValue" -> AbsType(ESValueT, EMPTY),
    )),

    // environment records
    I("EnvironmentRecord", Map(
      "OuterEnv" -> AbsType(NameT("EnvironmentRecord"), Null),
      "SubMap" -> MapT(NameT("Binding")),
    )),
    I("Binding", Map(
      "BoundValue" -> ESValueT,
      "Initialized" -> BoolT,
      "Mutable" -> BoolT,
    )),
    I("DeclarativeEnvironmentRecord", parent = "EnvironmentRecord", Map(
      "HasBinding" -> getClo("DeclarativeEnvironmentRecord.HasBinding"),
      "CreateMutableBinding" -> getClo("DeclarativeEnvironmentRecord.CreateMutableBinding"),
      "CreateImmutableBinding" -> getClo("DeclarativeEnvironmentRecord.CreateImmutableBinding"),
      "InitializeBinding" -> getClo("DeclarativeEnvironmentRecord.InitializeBinding"),
      "SetMutableBinding" -> getClo("DeclarativeEnvironmentRecord.SetMutableBinding"),
      "GetBindingValue" -> getClo("DeclarativeEnvironmentRecord.GetBindingValue"),
      "DeleteBinding" -> getClo("DeclarativeEnvironmentRecord.DeleteBinding"),
      "HasThisBinding" -> getClo("DeclarativeEnvironmentRecord.HasThisBinding"),
      "HasSuperBinding" -> getClo("DeclarativeEnvironmentRecord.HasSuperBinding"),
      "WithBaseObject" -> getClo("DeclarativeEnvironmentRecord.WithBaseObject"),
    )),
    I("ObjectEnvironmentRecord", parent = "EnvironmentRecord", Map(
      "withEnvironment" -> BoolT,
      "BindingObject" -> NameT("Object"),
      "HasBinding" -> getClo("ObjectEnvironmentRecord.HasBinding"),
      "CreateMutableBinding" -> getClo("ObjectEnvironmentRecord.CreateMutableBinding"),
      "InitializeBinding" -> getClo("ObjectEnvironmentRecord.InitializeBinding"),
      "SetMutableBinding" -> getClo("ObjectEnvironmentRecord.SetMutableBinding"),
      "GetBindingValue" -> getClo("ObjectEnvironmentRecord.GetBindingValue"),
      "DeleteBinding" -> getClo("ObjectEnvironmentRecord.DeleteBinding"),
      "HasThisBinding" -> getClo("ObjectEnvironmentRecord.HasThisBinding"),
      "HasSuperBinding" -> getClo("ObjectEnvironmentRecord.HasSuperBinding"),
      "WithBaseObject" -> getClo("ObjectEnvironmentRecord.WithBaseObject"),
    )),
    I("FunctionEnvironmentRecord", parent = "DeclarativeEnvironmentRecord", Map(
      "ThisValue" -> ESValueT,
      "ThisBindingStatus" -> AbsType(LEXICAL, INITIALIZED, UNINITIALIZED),
      "FunctionObject" -> NameT("Object"),
      "NewTarget" -> AbsType(NameT("Object"), Undef),
      "BindThisValue" -> getClo("FunctionEnvironmentRecord.BindThisValue"),
      "HasThisBinding" -> getClo("FunctionEnvironmentRecord.HasThisBinding"),
      "HasSuperBinding" -> getClo("FunctionEnvironmentRecord.HasSuperBinding"),
      "GetThisBinding" -> getClo("FunctionEnvironmentRecord.GetThisBinding"),
      "GetSuperBase" -> getClo("FunctionEnvironmentRecord.GetSuperBase"),
    )),
    I("GlobalEnvironmentRecord", parent = "EnvironmentRecord", Map(
      "OuterEnv" -> Null,
      "ObjectRecord" -> NameT("ObjectEnvironmentRecord"),
      "GlobalThisValue" -> NameT("Object"),
      "DeclarativeRecord" -> NameT("DeclarativeEnvironmentRecord"),
      "VarNames" -> ListT(StrT),
      "HasBinding" -> getClo("GlobalEnvironmentRecord.HasBinding"),
      "CreateMutableBinding" -> getClo("GlobalEnvironmentRecord.CreateMutableBinding"),
      "CreateImmutableBinding" -> getClo("GlobalEnvironmentRecord.CreateImmutableBinding"),
      "InitializeBinding" -> getClo("GlobalEnvironmentRecord.InitializeBinding"),
      "SetMutableBinding" -> getClo("GlobalEnvironmentRecord.SetMutableBinding"),
      "GetBindingValue" -> getClo("GlobalEnvironmentRecord.GetBindingValue"),
      "DeleteBinding" -> getClo("GlobalEnvironmentRecord.DeleteBinding"),
      "HasThisBinding" -> getClo("GlobalEnvironmentRecord.HasThisBinding"),
      "HasSuperBinding" -> getClo("GlobalEnvironmentRecord.HasSuperBinding"),
      "WithBaseObject" -> getClo("GlobalEnvironmentRecord.WithBaseObject"),
      "GetThisBinding" -> getClo("GlobalEnvironmentRecord.GetThisBinding"),
      "HasVarDeclaration" -> getClo("GlobalEnvironmentRecord.HasVarDeclaration"),
      "HasLexicalDeclaration" -> getClo("GlobalEnvironmentRecord.HasLexicalDeclaration"),
      "HasRestrictedGlobalProperty" -> getClo("GlobalEnvironmentRecord.HasRestrictedGlobalProperty"),
      "CanDeclareGlobalVar" -> getClo("GlobalEnvironmentRecord.CanDeclareGlobalVar"),
      "CanDeclareGlobalFunction" -> getClo("GlobalEnvironmentRecord.CanDeclareGlobalFunction"),
      "CreateGlobalVarBinding" -> getClo("GlobalEnvironmentRecord.CreateGlobalVarBinding"),
      "CreateGlobalFunctionBinding" -> getClo("GlobalEnvironmentRecord.CreateGlobalFunctionBinding"),
    )),
    I("ModuleEnvironmentRecord", parent = "DeclarativeEnvironmentRecord", Map(
      "OuterEnv" -> NameT("GlobalEnvironmentRecord"),
      "GetBindingValue" -> getClo("ModuleEnvironmentRecord.GetBindingValue"),
      "HasThisBinding" -> getClo("ModuleEnvironmentRecord.HasThisBinding"),
      "GetThisBinding" -> getClo("ModuleEnvironmentRecord.GetThisBinding"),
      "CreateImportBinding" -> getClo("ModuleEnvironmentRecord.CreateImportBinding"),
    )),

    // execution contexts
    I("ExecutionContext", Map(
      "Function" -> AbsType(NameT("FunctionObject"), Null),
      "Realm" -> NameT("RealmRecord"),
      "ScriptOrModule" -> AbsType(NameT("ScriptRecord"), NameT("ModuleRecord")),
      "LexicalEnvironment" -> NameT("EnvironmentRecord"),
      "VariableEnvironment" -> NameT("EnvironmentRecord"),
      "Generator" -> NameT("Object"),
    )),

    // job callback records
    I("JobCallbackRecord", Map(
      "Callback" -> NameT("FunctionObject"),
      "HostDefined" -> EMPTY,
    )),

    // agent records
    I("AgentRecord", Map(
      "LittleEndian" -> BoolT,
      "CanBlock" -> BoolT,
      "Signifier" -> Undef,
      "IsLockFree1" -> BoolT,
      "IsLockFree2" -> BoolT,
      "IsLockFree3" -> BoolT,
      "CandidateExecution" -> NameT("CandidateExecutionRecord"),
      "KeptAlive" -> ListT(NameT("Object")),
    )),
    I("CandidateExecutionRecord", Map(
      "EventsRecords" -> NilT,
      "ChosenValues" -> NilT,
      "AgentOrder" -> Undef,
      "ReadsBytesFrom" -> Undef,
      "ReadsFrom" -> Undef,
      "HostSynchronizesWith" -> Undef,
      "SynchronizesWith" -> Undef,
      "HappensBefore" -> Undef,
    )),

    // script records
    I("ScriptRecord", Map(
      "Realm" -> AbsType(NameT("RealmRecord"), Undef),
      "Environment" -> AbsType(NameT("EnvironmentRecord"), Undef),
      "ECMAScriptCode" -> AstT("Script"),
      "HostDefined" -> EMPTY,
    )),

    // module record
    I("ModuleRecord", Map(
      "Realm" -> AbsType(NameT("RealmRecord"), Undef),
      "Environment" -> AbsType(NameT("ModuleEnvironmentRecord"), Undef),
      "Namespace" -> AbsType(NameT("Object"), Undef),
      "HostDefined" -> EMPTY,
    )),
    I("CyclicModuleRecord", parent = "ModuleRecord", Map(
      "Status" -> AbsType(UNLINKED, LINKING, LINKED, EVALUATING, EVALUATED),
      "EvaluationError" -> AbsType(AbruptT, Undef),
      "DFSIndex" -> AbsType(NumT, Undef),
      "DFSAncestorIndex" -> AbsType(NumT, Undef),
      "RequestedModules" -> ListT(StrT),
    )),
    I("SourceTextModuleRecord", parent = "CyclicModuleRecord", Map(
      "ECMAScriptCode" -> AstT("Module"),
      "Context" -> NameT("ExecutionContext"),
      "ImportMeta" -> AbsType(NameT("Object"), EMPTY),
      "ImportEntries" -> ListT(NameT("ImportEntryRecord")),
      "LocalExportEntries" -> ListT(NameT("ExportEntryRecord")),
      "IndirectExportEntries" -> ListT(NameT("ExportEntryRecord")),
      "StarExportEntries" -> ListT(NameT("ExportEntryRecord")),
    )),
    I("ImportEntryRecord", Map(
      "ModuleRequest" -> StrT,
      "ImportName" -> StrT,
      "LocalName" -> StrT,
    )),
    I("ExportEntryRecord", Map(
      "ExportName" -> AbsType(StrT, Null),
      "ModuleRequest" -> AbsType(StrT, Null),
      "ImportName" -> AbsType(StrT, Null),
      "LocalName" -> AbsType(StrT, Null),
    )),
    I("PrimitiveMethod", Map(
      "Number" -> NameT("NumberMethod"),
      "BigInt" -> NameT("BigIntMethod"),
    )),
    I("NumberMethod", Map(
      "unit" -> Num(1),
      "unaryMinus" -> getClo("Number::unaryMinus"),
      "bitwiseNOT" -> getClo("Number::bitwiseNOT"),
      "exponentiate" -> getClo("Number::exponentiate"),
      "multiply" -> getClo("Number::multiply"),
      "divide" -> getClo("Number::divide"),
      "remainder" -> getClo("Number::remainder"),
      "add" -> getClo("Number::add"),
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
    I("BigIntMethod", Map(
      "unit" -> BigInt(1),
      "unaryMinus" -> getClo("BigInt::unaryMinus"),
      "bitwiseNOT" -> getClo("BigInt::bitwiseNOT"),
      "exponentiate" -> getClo("BigInt::exponentiate"),
      "multiply" -> getClo("BigInt::multiply"),
      "divide" -> getClo("BigInt::divide"),
      "remainder" -> getClo("BigInt::remainder"),
      "add" -> getClo("BigInt::add"),
      "subtract" -> getClo("BigInt::subtract"),
      "leftShift" -> getClo("BigInt::leftShift"),
      "signedRightShift" -> getClo("BigInt::signedRightShift"),
      "unsignedRightShift" -> getClo("BigInt::unsignedRightShift"),
      "lessThan" -> getClo("BigInt::lessThan"),
      "equal" -> getClo("BigInt::equal"),
      "sameValue" -> getClo("BigInt::sameValue"),
      "sameValueZero" -> getClo("BigInt::sameValueZero"),
      "bitwiseAND" -> getClo("BigInt::bitwiseAND"),
      "bitwiseXOR" -> getClo("BigInt::bitwiseXOR"),
      "bitwiseOR" -> getClo("BigInt::bitwiseOR"),
      "toString" -> getClo("BigInt::toString"),
    )),
  )
}
