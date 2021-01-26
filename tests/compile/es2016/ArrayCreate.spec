          1. Assert: _length_ is an integer Number ≥ 0.
          1. If _length_ is *-0*, let _length_ be *+0*.
          1. If _length_>2<sup>32</sup>-1, throw a *RangeError* exception.
          1. If the _proto_ argument was not passed, let _proto_ be the intrinsic object %ArrayPrototype%.
          1. Let _A_ be a newly created Array exotic object.
          1. Set _A_'s essential internal methods except for [[DefineOwnProperty]] to the default ordinary object definitions specified in <emu-xref href="#sec-ordinary-object-internal-methods-and-internal-slots"></emu-xref>.
          1. Set the [[DefineOwnProperty]] internal method of _A_ as specified in <emu-xref href="#sec-array-exotic-objects-defineownproperty-p-desc"></emu-xref>.
          1. Set the [[Prototype]] internal slot of _A_ to _proto_.
          1. Set the [[Extensible]] internal slot of _A_ to *true*.
          1. Perform ! OrdinaryDefineOwnProperty(_A_, `"length"`, PropertyDescriptor{[[Value]]: _length_, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *false*}).
          1. Return _A_.