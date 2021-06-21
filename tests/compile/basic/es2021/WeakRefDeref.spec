          1. Let _target_ be _weakRef_.[[WeakRefTarget]].
          1. If _target_ is not ~empty~, then
            1. Perform ! AddToKeptObjects(_target_).
            1. Return _target_.
          1. Return *undefined*.