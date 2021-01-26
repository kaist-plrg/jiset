          1. ReturnIfAbrupt(_V_).
          1. If Type(_V_) is not Reference, return _V_.
          1. Let _base_ be GetBase(_V_).
          1. If IsUnresolvableReference(_V_) is *true*, throw a *ReferenceError* exception.
          1. If IsPropertyReference(_V_) is *true*, then
            1. If HasPrimitiveBase(_V_) is *true*, then
              1. Assert: In this case, _base_ will never be *undefined* or *null*.
              1. Set _base_ to ! ToObject(_base_).
            1. Return ? _base_.[[Get]](GetReferencedName(_V_), GetThisValue(_V_)).
          1. Else,
            1. Assert: _base_ is an Environment Record.
            1. Return ? _base_.GetBindingValue(GetReferencedName(_V_), IsStrictReference(_V_)) (see <emu-xref href="#sec-environment-records"></emu-xref>).