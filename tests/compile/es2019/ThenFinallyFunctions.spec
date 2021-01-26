            1. Let _F_ be the active function object.
            1. Let _onFinally_ be _F_.[[OnFinally]].
            1. Assert: IsCallable(_onFinally_) is *true*.
            1. Let _result_ be ? Call(_onFinally_, *undefined*).
            1. Let _C_ be _F_.[[Constructor]].
            1. Assert: IsConstructor(_C_) is *true*.
            1. Let _promise_ be ? PromiseResolve(_C_, _result_).
            1. Let _valueThunk_ be equivalent to a function that returns _value_.
            1. Return ? Invoke(_promise_, `"then"`, « _valueThunk_ »).