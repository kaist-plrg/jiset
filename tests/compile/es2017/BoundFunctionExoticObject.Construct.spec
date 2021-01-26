          1. Let _target_ be _F_.[[BoundTargetFunction]].
          1. Assert: _target_ has a [[Construct]] internal method.
          1. Let _boundArgs_ be _F_.[[BoundArguments]].
          1. Let _args_ be a new list containing the same values as the list _boundArgs_ in the same order followed by the same values as the list _argumentsList_ in the same order.
          1. If SameValue(_F_, _newTarget_) is *true*, set _newTarget_ to _target_.
          1. Return ? Construct(_target_, _args_, _newTarget_).