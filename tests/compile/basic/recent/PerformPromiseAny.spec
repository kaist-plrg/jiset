            1. Assert: ! IsConstructor(_constructor_) is *true*.
            1. Assert: ! IsCallable(_promiseResolve_) is *true*.
            1. Let _errors_ be a new empty List.
            1. Let _remainingElementsCount_ be the Record { [[Value]]: 1 }.
            1. Let _index_ be 0.
            1. Repeat,
              1. Let _next_ be IteratorStep(_iteratorRecord_).
              1. If _next_ is an abrupt completion, set _iteratorRecord_.[[Done]] to *true*.
              1. ReturnIfAbrupt(_next_).
              1. If _next_ is *false*, then
                1. Set _iteratorRecord_.[[Done]] to *true*.
                1. Set _remainingElementsCount_.[[Value]] to _remainingElementsCount_.[[Value]] - 1.
                1. If _remainingElementsCount_.[[Value]] is 0, then
                  1. Let _error_ be a newly created `AggregateError` object.
                  1. Perform ! DefinePropertyOrThrow(_error_, *"errors"*, PropertyDescriptor { [[Configurable]]: *true*, [[Enumerable]]: *false*, [[Writable]]: *true*, [[Value]]: ! CreateArrayFromList(_errors_) }).
                  1. Return ThrowCompletion(_error_).
                1. Return _resultCapability_.[[Promise]].
              1. Let _nextValue_ be IteratorValue(_next_).
              1. If _nextValue_ is an abrupt completion, set _iteratorRecord_.[[Done]] to *true*.
              1. ReturnIfAbrupt(_nextValue_).
              1. Append *undefined* to _errors_.
              1. Let _nextPromise_ be ? Call(_promiseResolve_, _constructor_, « _nextValue_ »).
              1. Let _stepsRejected_ be the algorithm steps defined in <emu-xref href="#sec-promise.any-reject-element-functions" title></emu-xref>.
              1. Let _lengthRejected_ be the number of non-optional parameters of the function definition in <emu-xref href="#sec-promise.any-reject-element-functions" title></emu-xref>.
              1. Let _onRejected_ be ! CreateBuiltinFunction(_stepsRejected_, _lengthRejected_, *""*, « [[AlreadyCalled]], [[Index]], [[Errors]], [[Capability]], [[RemainingElements]] »).
              1. Set _onRejected_.[[AlreadyCalled]] to *false*.
              1. Set _onRejected_.[[Index]] to _index_.
              1. Set _onRejected_.[[Errors]] to _errors_.
              1. Set _onRejected_.[[Capability]] to _resultCapability_.
              1. Set _onRejected_.[[RemainingElements]] to _remainingElementsCount_.
              1. Set _remainingElementsCount_.[[Value]] to _remainingElementsCount_.[[Value]] + 1.
              1. Perform ? Invoke(_nextPromise_, *"then"*, « _resultCapability_.[[Resolve]], _onRejected_ »).
              1. Set _index_ to _index_ + 1.