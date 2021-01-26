        1. Let _intrinsics_ be a new Record.
        1. Set _realmRec_.[[Intrinsics]] to _intrinsics_.
        1. Let _objProto_ be ObjectCreate(*null*).
        1. Set _intrinsics_.[[%ObjectPrototype%]] to _objProto_.
        1. Let _throwerSteps_ be the algorithm steps specified in <emu-xref href="#sec-%throwtypeerror%"></emu-xref> for the %ThrowTypeError% function.
        1. Let _thrower_ be CreateBuiltinFunction(_realmRec_, _throwerSteps_, *null*).
        1. Set _intrinsics_.[[%ThrowTypeError%]] to _thrower_.
        1. Let _noSteps_ be an empty sequence of algorithm steps.
        1. Let _funcProto_ be CreateBuiltinFunction(_realmRec_, _noSteps_, _objProto_).
        1. Set _intrinsics_.[[%FunctionPrototype%]] to _funcProto_.
        1. Call _thrower_.[[SetPrototypeOf]](_funcProto_).
        1. Perform AddRestrictedFunctionProperties(_funcProto_, _realmRec_).
        1. Set fields of _intrinsics_ with the values listed in <emu-xref href="#table-7"></emu-xref> that have not already been handled above. The field names are the names listed in column one of the table. The value of each field is a new object value fully and recursively populated with property values as defined by the specification of each object in clauses 18-26. All object property values are newly created object values. All values that are built-in function objects are created by performing CreateBuiltinFunction(_realmRec_, <steps>, <prototype>, <slots>) where <steps> is the definition of that function provided by this specification, <prototype> is the specified value of the function's [[Prototype]] internal slot and <slots> is a list of the names, if any, of the function's specified internal slots. The creation of the intrinsics and their properties must be ordered to avoid any dependencies upon objects that have not yet been created.
        1. Return _intrinsics_.