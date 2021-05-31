          1. Let _func_ be the *this* value.
          1. If IsCallable(_func_) is *false*, throw a *TypeError* exception.
          1. Perform PrepareForTailCall().
          1. [id="step-function-proto-call-call"] Return ? Call(_func_, _thisArg_, _args_).