          1. Assert: _internalSlotsList_ contains the names [[ViewedArrayBuffer]], [[ArrayLength]], [[ByteOffset]], and [[TypedArrayName]].
          1. Let _A_ be a newly created object with an internal slot for each name in _internalSlotsList_.
          1. Set _A_'s essential internal methods to the default ordinary object definitions specified in <emu-xref href="#sec-ordinary-object-internal-methods-and-internal-slots"></emu-xref>.
          1. Set _A_.[[GetOwnProperty]] as specified in <emu-xref href="#sec-integer-indexed-exotic-objects-getownproperty-p"></emu-xref>.
          1. Set _A_.[[HasProperty]] as specified in <emu-xref href="#sec-integer-indexed-exotic-objects-hasproperty-p"></emu-xref>.
          1. Set _A_.[[DefineOwnProperty]] as specified in <emu-xref href="#sec-integer-indexed-exotic-objects-defineownproperty-p-desc"></emu-xref>.
          1. Set _A_.[[Get]] as specified in <emu-xref href="#sec-integer-indexed-exotic-objects-get-p-receiver"></emu-xref>.
          1. Set _A_.[[Set]] as specified in <emu-xref href="#sec-integer-indexed-exotic-objects-set-p-v-receiver"></emu-xref>.
          1. Set _A_.[[OwnPropertyKeys]] as specified in <emu-xref href="#sec-integer-indexed-exotic-objects-ownpropertykeys"></emu-xref>.
          1. Set _A_.[[Prototype]] to _prototype_.
          1. Set _A_.[[Extensible]] to *true*.
          1. Return _A_.