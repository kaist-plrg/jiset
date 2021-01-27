          1. Assert: ! IsNonNegativeInteger(_length_) is *true*.
          1. If _length_ is *-0*, set _length_ to *+0*.
          1. If _length_ > 2<sup>32</sup> - 1, throw a *RangeError* exception.
          1. If _proto_ is not present, set _proto_ to %Array.prototype%.
          1. Let _A_ be ! MakeBasicObject(« [[Prototype]], [[Extensible]] »).
          1. Set _A_.[[Prototype]] to _proto_.
          1. Set _A_.[[DefineOwnProperty]] as specified in <emu-xref href="#sec-array-exotic-objects-defineownproperty-p-desc"></emu-xref>.
          1. Perform ! OrdinaryDefineOwnProperty(_A_, *"length"*, PropertyDescriptor { [[Value]]: _length_, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *false* }).
          1. Return _A_.