          1. ReturnIfAbrupt(_V_).
          1. ReturnIfAbrupt(_W_).
          1. If Type(_V_) is not Reference, throw a *ReferenceError* exception.
          1. Let _base_ be GetBase(_V_).
          1. If IsUnresolvableReference(_V_) is *true*, then
            1. If IsStrictReference(_V_) is *true*, then
              1. Throw a *ReferenceError* exception.
            1. Let _globalObj_ be GetGlobalObject().
            1. Return ? Set(_globalObj_, GetReferencedName(_V_), _W_, *false*).
          1. Else if IsPropertyReference(_V_) is *true*, then
            1. If HasPrimitiveBase(_V_) is *true*, then
              1. Assert: In this case, _base_ will never be *undefined* or *null*.
              1. Set _base_ to ! ToObject(_base_).
            1. Let _succeeded_ be ? _base_.[[Set]](GetReferencedName(_V_), _W_, GetThisValue(_V_)).
            1. If _succeeded_ is *false* and IsStrictReference(_V_) is *true*, throw a *TypeError* exception.
            1. Return.
          1. Else _base_ must be an Environment Record,
            1. Return ? _base_.SetMutableBinding(GetReferencedName(_V_), _W_, IsStrictReference(_V_)) (see <emu-xref href="#sec-environment-records"></emu-xref>).