          1. Let _newTarget_ be GetNewTarget().
          1. If _newTarget_ is *undefined*, throw a *ReferenceError* exception.
          1. Let _func_ be ? GetSuperConstructor().
          1. Let _argList_ be ArgumentListEvaluation of |Arguments|.
          1. ReturnIfAbrupt(_argList_).
          1. Let _result_ be ? Construct(_func_, _argList_, _newTarget_).
          1. Let _thisER_ be GetThisEnvironment( ).
          1. Return ? _thisER_.BindThisValue(_result_).