          1. Let _target_ be the value of _F_'s [[BoundTargetFunction]] internal slot.
          1. Assert: _target_ has a [[Construct]] internal method.
          1. Let _boundArgs_ be the value of _F_'s [[BoundArguments]] internal slot.
          1. Let _args_ be a new list containing the same values as the list _boundArgs_ in the same order followed by the same values as the list _argumentsList_ in the same order.
          1. If SameValue(_F_, _newTarget_) is *true*, let _newTarget_ be _target_.
          1. Return ? Construct(_target_, _args_, _newTarget_).