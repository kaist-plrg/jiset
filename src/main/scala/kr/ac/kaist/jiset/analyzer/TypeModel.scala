package kr.ac.kaist.jiset.analyzer

// TODO extract from specification
object TypeModel {
  import Type._

  lazy val infos: List[Info] = List(
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
      "FormalParameters" -> AbsType(AstT("FormalParameters"), AstT("ArrowParameters")),
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
      "StringData" -> StrT,
      "GetOwnProperty" -> getClo("StringExoticObject.GetOwnProperty"),
      "DefineOwnProperty" -> getClo("StringExoticObject.DefineOwnProperty"),
      "OwnPropertyKeys" -> getClo("StringExoticObject.OwnPropertyKeys"),
    )),
    I("ArgumentsExoticObject", parent = "Object", Map(
      "ParameterMap" -> AbsType(NameT("OrdinaryObject")),
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
      "ProxyHandler" -> AbsType(NameT("Object")),
      "ProxyTarget" -> AbsType(NameT("Object")),
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

    // special instances
    I("ForInIteratorInstance", parent = "OrdinaryObject", Map(
      "Object" -> NameT("Object"),
      "ObjectWasVisited" -> BoolT,
      "VisitedKeys" -> ListT(StrT),
      "RemainingKeys" -> ListT(StrT),
    )),
    I("AsynFromSyncIteratorInstance", parent = "OrdinaryObject", Map(
      "SyncIteratorRecord" -> RecordT(
        "Iterator" -> NameT("Object"),
        "NextMethod" -> ESValueT,
        "Done" -> BoolT,
      ),
    )),
    I("PromiseInstance", parent = "OrdinaryObject", Map(
      "PromiseState" -> AbsType(PENDING, FULFILLED, REJECTED),
      "PromiseResult" -> ESValueT,
      "PromiseFulfillReactions" -> ListT(NameT("PromiseReactionRecord")),
      "PromiseRejectReactions" -> ListT(NameT("PromiseReactionRecord")),
      "PromiseIsHandled" -> BoolT,
    )),
    I("GeneratorInstance", parent = "OrdinaryObject", Map(
      "GeneratorState" -> AbsType(Undef, SUSPENDED_START, SUSPENDED_YIELD, EXECUTING, COMPLETED),
      "GeneratorContext" -> NameT("ExecutionContext"),
      "GeneratorBrand" -> EMPTY,
    )),
    I("AsyncGeneratorInstance", parent = "OrdinaryObject", Map(
      "AsyncGeneratorState" -> AbsType(Undef, SUSPENDED_START, SUSPENDED_YIELD, EXECUTING, AWAITING_RETURN, COMPLETED),
      "AsyncGeneratorContext" -> NameT("ExecutionContext"),
      "AsyncGeneratorQueue" -> ListT(NameT("AsyncGeneratorRequestRecord")),
      "GeneratorBrand" -> EMPTY,
    )),

    // promise records
    I("PromiseReactionRecord", Map(
      "Capability" -> AbsType(NameT("PromiseCapabilityRecord"), Undef),
      "Type" -> AbsType(FULFILL, REJECT),
      "Handler" -> AbsType(NameT("JobCallbackRecord"), EMPTY),
    )),
    I("PromiseCapabilityRecord", Map(
      "Promise" -> NameT("Object"),
      "Resolve" -> NameT("FunctionObject"),
      "Reject" -> NameT("FunctionObject"),
    )),
    I("AsyncGeneratorRequestRecord", Map(
      "Completion" -> AbsType(NormalT(ESValueT), AbruptT),
      "Capability" -> NameT("PromiseCapabilityRecord"),
    )),

    // reference records
    I("ReferenceRecord", Map(
      "Base" -> AbsType(ESValueT, NameT("EnvironmentRecord"), UNRESOLVABLE),
      "ReferencedName" -> AbsType(StrT, SymbolT),
      "Strict" -> BoolT,
      "ThisValue" -> AbsType(ESValueT, EMPTY),
    )),

    // environment records
    I("LexicalEnvironment", Map(
      "EnvironmentRecord" -> NameT("EnvironmentRecord"),
    )),
    I("EnvironmentRecord", parent = "LexicalEnvironment", Map(
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
      "HostDefined" -> Undef,
      "GetExportedNames" -> getClo("ModuleRecord.GetExportedNames"),
      "ResolveExport" -> getClo("ModuleRecord.ResolveExport"),
      "Link" -> getClo("ModuleRecord.Link"),
      "Evaluate" -> getClo("ModuleRecord.Evaluate"),
    )),
    I("ResolvedBindingRecord", Map(
      "Module" -> NameT("ModuleRecord"),
      "BindingName" -> StrT
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

  // get function closure by name
  private lazy val cloMap: Map[String, AbsType] =
    (for (func <- cfg.funcs) yield func.name -> CloT(func.uid).abs).toMap
  private def getClo(name: String): AbsType = cloMap.getOrElse(name, {
    warning(s"unknown function name: $name")
    Absent
  })
}
