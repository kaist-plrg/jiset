          1. Let _target_ be _F_.[[BoundTargetFunction]].
          1. Assert: IsConstructor(_target_) is *true*.
          1. Let _boundArgs_ be _F_.[[BoundArguments]].
          1. Let _args_ be a List whose elements are the elements of _boundArgs_, followed by the elements of _argumentsList_.
          1. If SameValue(_F_, _newTarget_) is *true*, set _newTarget_ to _target_.
          1. Return ? Construct(_target_, _args_, _newTarget_).