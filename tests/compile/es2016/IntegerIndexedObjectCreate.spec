          1. Assert: _internalSlotsList_ contains the names [[ViewedArrayBuffer]], [[ArrayLength]], [[ByteOffset]], and [[TypedArrayName]].
          1. Let _A_ be a newly created object with an internal slot for each name in _internalSlotsList_.
          1. Set _A_'s essential internal methods to the default ordinary object definitions specified in <emu-xref href="#sec-ordinary-object-internal-methods-and-internal-slots"></emu-xref>.
          1. Set the [[GetOwnProperty]] internal method of _A_ as specified in <emu-xref href="#sec-integer-indexed-exotic-objects-getownproperty-p"></emu-xref>.
          1. Set the [[HasProperty]] internal method of _A_ as specified in <emu-xref href="#sec-integer-indexed-exotic-objects-hasproperty-p"></emu-xref>.
          1. Set the [[DefineOwnProperty]] internal method of _A_ as specified in <emu-xref href="#sec-integer-indexed-exotic-objects-defineownproperty-p-desc"></emu-xref>.
          1. Set the [[Get]] internal method of _A_ as specified in <emu-xref href="#sec-integer-indexed-exotic-objects-get-p-receiver"></emu-xref>.
          1. Set the [[Set]] internal method of _A_ as specified in <emu-xref href="#sec-integer-indexed-exotic-objects-set-p-v-receiver"></emu-xref>.
          1. Set the [[OwnPropertyKeys]] internal method of _A_ as specified in <emu-xref href="#sec-integer-indexed-exotic-objects-ownpropertykeys"></emu-xref>.
          1. Set the [[Prototype]] internal slot of _A_ to _prototype_.
          1. Set the [[Extensible]] internal slot of _A_ to *true*.
          1. Return _A_.