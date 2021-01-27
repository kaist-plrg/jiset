        1. If IsConstructor(_target_) is *false*, throw a *TypeError* exception.
        1. If _newTarget_ is not present, let _newTarget_ be _target_.
        1. Else, if IsConstructor(_newTarget_) is *false*, throw a *TypeError* exception.
        1. Let _args_ be ? CreateListFromArrayLike(_argumentsList_).
        1. Return ? Construct(_target_, _args_, _newTarget_).