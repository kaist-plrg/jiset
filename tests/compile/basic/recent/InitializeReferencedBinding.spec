          1. ReturnIfAbrupt(_V_).
          1. ReturnIfAbrupt(_W_).
          1. Assert: _V_ is a Reference Record.
          1. Assert: IsUnresolvableReference(_V_) is *false*.
          1. Let _base_ be _V_.[[Base]].
          1. Assert: _base_ is an Environment Record.
          1. Return _base_.InitializeBinding(_V_.[[ReferencedName]], _W_).