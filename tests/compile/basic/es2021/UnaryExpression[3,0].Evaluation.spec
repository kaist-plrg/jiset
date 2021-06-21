          1. Let _val_ be the result of evaluating |UnaryExpression|.
          1. If _val_ is a Reference Record, then
            1. If IsUnresolvableReference(_val_) is *true*, return *"undefined"*.
          1. Set _val_ to ? GetValue(_val_).
          1. Return a String according to <emu-xref href="#table-typeof-operator-results"></emu-xref>.