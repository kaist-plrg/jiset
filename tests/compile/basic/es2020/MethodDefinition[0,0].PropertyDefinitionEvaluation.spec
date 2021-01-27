        1. Let _methodDef_ be ? DefineMethod of |MethodDefinition| with argument _object_.
        1. Perform SetFunctionName(_methodDef_.[[Closure]], _methodDef_.[[Key]]).
        1. Let _desc_ be the PropertyDescriptor { [[Value]]: _methodDef_.[[Closure]], [[Writable]]: *true*, [[Enumerable]]: _enumerable_, [[Configurable]]: *true* }.
        1. Return ? DefinePropertyOrThrow(_object_, _methodDef_.[[Key]], _desc_).