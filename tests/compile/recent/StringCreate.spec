          1. Let _S_ be ! MakeBasicObject(« [[Prototype]], [[Extensible]], [[StringData]] »).
          1. Set _S_.[[Prototype]] to _prototype_.
          1. Set _S_.[[StringData]] to _value_.
          1. Set _S_.[[GetOwnProperty]] as specified in <emu-xref href="#sec-string-exotic-objects-getownproperty-p"></emu-xref>.
          1. Set _S_.[[DefineOwnProperty]] as specified in <emu-xref href="#sec-string-exotic-objects-defineownproperty-p-desc"></emu-xref>.
          1. Set _S_.[[OwnPropertyKeys]] as specified in <emu-xref href="#sec-string-exotic-objects-ownpropertykeys"></emu-xref>.
          1. Let _length_ be the number of code unit elements in _value_.
          1. Perform ! DefinePropertyOrThrow(_S_, *"length"*, PropertyDescriptor { [[Value]]: 𝔽(_length_), [[Writable]]: *false*, [[Enumerable]]: *false*, [[Configurable]]: *false* }).
          1. Return _S_.