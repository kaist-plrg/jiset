          1. Let _val_ be the result of evaluating |UnaryExpression|.
          1. If Type(_val_) is Reference, then
            1. If IsUnresolvableReference(_val_) is *true*, return `"undefined"`.
          1. Set _val_ to ? GetValue(_val_).
          1. Return a String according to <emu-xref href="#table-35"></emu-xref>.