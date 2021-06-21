          1. Let _newTarget_ be GetNewTarget().
          1. Assert: Type(_newTarget_) is Object.
          1. Let _func_ be ! GetSuperConstructor().
          1. Let _argList_ be ? ArgumentListEvaluation of |Arguments|.
          1. If IsConstructor(_func_) is *false*, throw a *TypeError* exception.
          1. Let _result_ be ? Construct(_func_, _argList_, _newTarget_).
          1. Let _thisER_ be GetThisEnvironment().
          1. Return ? _thisER_.BindThisValue(_result_).