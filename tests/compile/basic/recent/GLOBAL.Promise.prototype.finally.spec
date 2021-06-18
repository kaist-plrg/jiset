          1. Let _promise_ be the *this* value.
          1. If Type(_promise_) is not Object, throw a *TypeError* exception.
          1. Let _C_ be ? SpeciesConstructor(_promise_, %Promise%).
          1. Assert: IsConstructor(_C_) is *true*.
          1. If IsCallable(_onFinally_) is *false*, then
            1. Let _thenFinally_ be _onFinally_.
            1. Let _catchFinally_ be _onFinally_.
          1. Else,
            1. Let _stepsThenFinally_ be the algorithm steps defined in <emu-xref href="#sec-thenfinallyfunctions" title></emu-xref>.
            1. Let _lengthThenFinally_ be the number of non-optional parameters of the function definition in <emu-xref href="#sec-thenfinallyfunctions" title></emu-xref>.
            1. Let _thenFinally_ be ! CreateBuiltinFunction(_stepsThenFinally_, _lengthThenFinally_, *""*, « [[Constructor]], [[OnFinally]] »).
            1. Set _thenFinally_.[[Constructor]] to _C_.
            1. Set _thenFinally_.[[OnFinally]] to _onFinally_.
            1. Let _stepsCatchFinally_ be the algorithm steps defined in <emu-xref href="#sec-catchfinallyfunctions" title></emu-xref>.
            1. Let _lengthCatchFinally_ be the number of non-optional parameters of the function definition in <emu-xref href="#sec-catchfinallyfunctions" title></emu-xref>.
            1. Let _catchFinally_ be ! CreateBuiltinFunction(_stepsCatchFinally_, _lengthCatchFinally_, *""*, « [[Constructor]], [[OnFinally]] »).
            1. Set _catchFinally_.[[Constructor]] to _C_.
            1. Set _catchFinally_.[[OnFinally]] to _onFinally_.
          1. Return ? Invoke(_promise_, *"then"*, « _thenFinally_, _catchFinally_ »).