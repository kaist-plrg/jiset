          1. Assert: _length_ is an integer Number ≥ 0.
          1. If _length_ is *-0*, set _length_ to *+0*.
          1. If _length_>2<sup>32</sup>-1, throw a *RangeError* exception.
          1. If the _proto_ argument was not passed, set _proto_ to the intrinsic object %ArrayPrototype%.
          1. Let _A_ be a newly created Array exotic object.
          1. Set _A_'s essential internal methods except for [[DefineOwnProperty]] to the default ordinary object definitions specified in <emu-xref href="#sec-ordinary-object-internal-methods-and-internal-slots"></emu-xref>.
          1. Set _A_.[[DefineOwnProperty]] as specified in <emu-xref href="#sec-array-exotic-objects-defineownproperty-p-desc"></emu-xref>.
          1. Set _A_.[[Prototype]] to _proto_.
          1. Set _A_.[[Extensible]] to *true*.
          1. Perform ! OrdinaryDefineOwnProperty(_A_, `"length"`, PropertyDescriptor{[[Value]]: _length_, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *false*}).
          1. Return _A_.