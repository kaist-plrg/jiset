            1. Assert: This call to Evaluate is not happening at the same time as another call to Evaluate within the surrounding agent.
            1. Let _module_ be this Cyclic Module Record.
            1. Assert: _module_.[[Status]] is ~linked~ or ~evaluated~.
            1. Let _stack_ be a new empty List.
            1. Let _result_ be InnerModuleEvaluation(_module_, _stack_, 0).
            1. If _result_ is an abrupt completion, then
              1. For each Cyclic Module Record _m_ in _stack_, do
                1. Assert: _m_.[[Status]] is ~evaluating~.
                1. Set _m_.[[Status]] to ~evaluated~.
                1. Set _m_.[[EvaluationError]] to _result_.
              1. Assert: _module_.[[Status]] is ~evaluated~ and _module_.[[EvaluationError]] is _result_.
              1. Return _result_.
            1. Assert: _module_.[[Status]] is ~evaluated~ and _module_.[[EvaluationError]] is *undefined*.
            1. Assert: _stack_ is empty.
            1. Return *undefined*.