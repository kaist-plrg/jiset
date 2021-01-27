        1. Let _generatorKind_ be ! GetGeneratorKind().
        1. Let _exprRef_ be the result of evaluating |AssignmentExpression|.
        1. Let _value_ be ? GetValue(_exprRef_).
        1. Let _iteratorRecord_ be ? GetIterator(_value_, _generatorKind_).
        1. Let _iterator_ be _iteratorRecord_.[[Iterator]].
        1. Let _received_ be NormalCompletion(*undefined*).
        1. Repeat,
          1. If _received_.[[Type]] is ~normal~, then
            1. Let _innerResult_ be ? Call(_iteratorRecord_.[[NextMethod]], _iteratorRecord_.[[Iterator]], « _received_.[[Value]] »).
            1. If _generatorKind_ is ~async~, then set _innerResult_ to ? Await(_innerResult_).
            1. If Type(_innerResult_) is not Object, throw a *TypeError* exception.
            1. Let _done_ be ? IteratorComplete(_innerResult_).
            1. If _done_ is *true*, then
              1. Return ? IteratorValue(_innerResult_).
            1. If _generatorKind_ is ~async~, then set _received_ to AsyncGeneratorYield(? IteratorValue(_innerResult_)).
            1. Else, set _received_ to GeneratorYield(_innerResult_).
          1. Else if _received_.[[Type]] is ~throw~, then
            1. Let _throw_ be ? GetMethod(_iterator_, *"throw"*).
            1. If _throw_ is not *undefined*, then
              1. Let _innerResult_ be ? Call(_throw_, _iterator_, « _received_.[[Value]] »).
              1. If _generatorKind_ is ~async~, then set _innerResult_ to ? Await(_innerResult_).
              1. NOTE: Exceptions from the inner iterator `throw` method are propagated. Normal completions from an inner `throw` method are processed similarly to an inner `next`.
              1. If Type(_innerResult_) is not Object, throw a *TypeError* exception.
              1. Let _done_ be ? IteratorComplete(_innerResult_).
              1. If _done_ is *true*, then
                1. Return ? IteratorValue(_innerResult_).
              1. If _generatorKind_ is ~async~, then set _received_ to AsyncGeneratorYield(? IteratorValue(_innerResult_)).
              1. Else, set _received_ to GeneratorYield(_innerResult_).
            1. Else,
              1. NOTE: If _iterator_ does not have a `throw` method, this throw is going to terminate the `yield*` loop. But first we need to give _iterator_ a chance to clean up.
              1. Let _closeCompletion_ be Completion { [[Type]]: ~normal~, [[Value]]: ~empty~, [[Target]]: ~empty~ }.
              1. If _generatorKind_ is ~async~, perform ? AsyncIteratorClose(_iteratorRecord_, _closeCompletion_).
              1. Else, perform ? IteratorClose(_iteratorRecord_, _closeCompletion_).
              1. NOTE: The next step throws a *TypeError* to indicate that there was a `yield*` protocol violation: _iterator_ does not have a `throw` method.
              1. Throw a *TypeError* exception.
          1. Else,
            1. Assert: _received_.[[Type]] is ~return~.
            1. Let _return_ be ? GetMethod(_iterator_, *"return"*).
            1. If _return_ is *undefined*, then
              1. If _generatorKind_ is ~async~, then set _received_.[[Value]] to ? Await(_received_.[[Value]]).
              1. Return Completion(_received_).
            1. Let _innerReturnResult_ be ? Call(_return_, _iterator_, « _received_.[[Value]] »).
            1. If _generatorKind_ is ~async~, then set _innerReturnResult_ to ? Await(_innerReturnResult_).
            1. If Type(_innerReturnResult_) is not Object, throw a *TypeError* exception.
            1. Let _done_ be ? IteratorComplete(_innerReturnResult_).
            1. If _done_ is *true*, then
              1. Let _value_ be ? IteratorValue(_innerReturnResult_).
              1. Return Completion { [[Type]]: ~return~, [[Value]]: _value_, [[Target]]: ~empty~ }.
            1. If _generatorKind_ is ~async~, then set _received_ to AsyncGeneratorYield(? IteratorValue(_innerReturnResult_)).
            1. Else, set _received_ to GeneratorYield(_innerReturnResult_).