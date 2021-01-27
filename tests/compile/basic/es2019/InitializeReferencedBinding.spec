          1. ReturnIfAbrupt(_V_).
          1. ReturnIfAbrupt(_W_).
          1. Assert: Type(_V_) is Reference.
          1. Assert: IsUnresolvableReference(_V_) is *false*.
          1. Let _base_ be GetBase(_V_).
          1. Assert: _base_ is an Environment Record.
          1. Return _base_.InitializeBinding(GetReferencedName(_V_), _W_).