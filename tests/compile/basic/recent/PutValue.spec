          1. ReturnIfAbrupt(_V_).
          1. ReturnIfAbrupt(_W_).
          1. If _V_ is not a Reference Record, throw a *ReferenceError* exception.
          1. If IsUnresolvableReference(_V_) is *true*, then
            1. If _V_.[[Strict]] is *true*, throw a *ReferenceError* exception.
            1. Let _globalObj_ be GetGlobalObject().
            1. Return ? Set(_globalObj_, _V_.[[ReferencedName]], _W_, *false*).
          1. If IsPropertyReference(_V_) is *true*, then
            1. [id="step-putvalue-toobject"] Let _baseObj_ be ! ToObject(_V_.[[Base]]).
            1. Let _succeeded_ be ? _baseObj_.[[Set]](_V_.[[ReferencedName]], _W_, GetThisValue(_V_)).
            1. If _succeeded_ is *false* and _V_.[[Strict]] is *true*, throw a *TypeError* exception.
            1. Return.
          1. Else,
            1. Let _base_ be _V_.[[Base]].
            1. Assert: _base_ is an Environment Record.
            1. Return ? _base_.SetMutableBinding(_V_.[[ReferencedName]], _W_, _V_.[[Strict]]) (see <emu-xref href="#sec-environment-records"></emu-xref>).