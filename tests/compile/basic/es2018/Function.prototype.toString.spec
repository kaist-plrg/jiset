          1. If _func_ is a Bound Function exotic object, then
            1. Return an implementation-dependent String source code representation of _func_. The representation must conform to the rules below. It is implementation-dependent whether the representation includes bound function information or information about the target function.
          1. If Type(_func_) is Object and is either a built-in function object or has an [[ECMAScriptCode]] internal slot, then
            1. Return an implementation-dependent String source code representation of _func_. The representation must conform to the rules below.
          1. Throw a *TypeError* exception.