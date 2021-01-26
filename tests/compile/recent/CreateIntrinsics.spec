        1. Let _intrinsics_ be a new Record.
        1. Set _realmRec_.[[Intrinsics]] to _intrinsics_.
        1. Set fields of _intrinsics_ with the values listed in <emu-xref href="#table-well-known-intrinsic-objects"></emu-xref>. The field names are the names listed in column one of the table. The value of each field is a new object value fully and recursively populated with property values as defined by the specification of each object in clauses <emu-xref href="#sec-global-object"></emu-xref> through <emu-xref href="#sec-reflection"></emu-xref>. All object property values are newly created object values. All values that are built-in function objects are created by performing CreateBuiltinFunction(_steps_, _slots_, _realmRec_, _prototype_) where _steps_ is the definition of that function provided by this specification, _slots_ is a list of the names, if any, of the function's specified internal slots, and _prototype_ is the specified value of the function's [[Prototype]] internal slot. The creation of the intrinsics and their properties must be ordered to avoid any dependencies upon objects that have not yet been created.
        1. Perform AddRestrictedFunctionProperties(_intrinsics_.[[%Function.prototype%]], _realmRec_).
        1. Return _intrinsics_.