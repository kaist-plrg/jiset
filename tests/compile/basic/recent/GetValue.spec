          1. ReturnIfAbrupt(_V_).
          1. If _V_ is not a Reference Record, return _V_.
          1. If IsUnresolvableReference(_V_) is *true*, throw a *ReferenceError* exception.
          1. If IsPropertyReference(_V_) is *true*, then
            1. [id="step-getvalue-toobject"] Let _baseObj_ be ! ToObject(_V_.[[Base]]).
            1. Return ? _baseObj_.[[Get]](_V_.[[ReferencedName]], GetThisValue(_V_)).
          1. Else,
            1. Let _base_ be _V_.[[Base]].
            1. Assert: _base_ is an Environment Record.
            1. Return ? _base_.GetBindingValue(_V_.[[ReferencedName]], _V_.[[Strict]]) (see <emu-xref href="#sec-environment-records"></emu-xref>).