            1. Assert: IsConstructor(_constructor_) is *true*.
            1. Assert: _resultCapability_ is a PromiseCapability Record.
            1. Repeat,
              1. Let _next_ be IteratorStep(_iteratorRecord_).
              1. If _next_ is an abrupt completion, set _iteratorRecord_.[[Done]] to *true*.
              1. ReturnIfAbrupt(_next_).
              1. If _next_ is *false*, then
                1. Set _iteratorRecord_.[[Done]] to *true*.
                1. Return _resultCapability_.[[Promise]].
              1. Let _nextValue_ be IteratorValue(_next_).
              1. If _nextValue_ is an abrupt completion, set _iteratorRecord_.[[Done]] to *true*.
              1. ReturnIfAbrupt(_nextValue_).
              1. Let _nextPromise_ be ? Invoke(_constructor_, `"resolve"`, « _nextValue_ »).
              1. Perform ? Invoke(_nextPromise_, `"then"`, « _resultCapability_.[[Resolve]], _resultCapability_.[[Reject]] »).