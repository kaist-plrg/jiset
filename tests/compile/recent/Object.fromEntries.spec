          1. Perform ? RequireObjectCoercible(_iterable_).
          1. Let _obj_ be ! OrdinaryObjectCreate(%Object.prototype%).
          1. Assert: _obj_ is an extensible ordinary object with no own properties.
          1. Let _stepsDefine_ be the algorithm steps defined in <emu-xref href="#sec-create-data-property-on-object-functions" title></emu-xref>.
          1. Let _adder_ be ! CreateBuiltinFunction(_stepsDefine_, « »).
          1. Return ? AddEntriesFromIterable(_obj_, _iterable_, _adder_).