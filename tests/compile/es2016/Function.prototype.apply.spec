          1. If IsCallable(_func_) is *false*, throw a *TypeError* exception.
          1. If _argArray_ is *null* or *undefined*, then
            1. Perform PrepareForTailCall().
            1. Return ? Call(_func_, _thisArg_).
          1. Let _argList_ be ? CreateListFromArrayLike(_argArray_).
          1. Perform PrepareForTailCall().
          1. Return ? Call(_func_, _thisArg_, _argList_).