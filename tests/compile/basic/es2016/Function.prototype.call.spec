          1. If IsCallable(_func_) is *false*, throw a *TypeError* exception.
          1. Let _argList_ be a new empty List.
          1. If this method was called with more than one argument, then in left to right order, starting with the second argument, append each argument as the last element of _argList_.
          1. Perform PrepareForTailCall().
          1. Return ? Call(_func_, _thisArg_, _argList_).