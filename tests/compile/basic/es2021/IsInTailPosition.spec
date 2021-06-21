        1. Assert: _call_ is a Parse Node.
        1. If the source code matching _call_ is non-strict code, return *false*.
        1. If _call_ is not contained within a |FunctionBody|, |ConciseBody|, or |AsyncConciseBody|, return *false*.
        1. Let _body_ be the |FunctionBody|, |ConciseBody|, or |AsyncConciseBody| that most closely contains _call_.
        1. If _body_ is the |FunctionBody| of a |GeneratorBody|, return *false*.
        1. If _body_ is the |FunctionBody| of an |AsyncFunctionBody|, return *false*.
        1. If _body_ is the |FunctionBody| of an |AsyncGeneratorBody|, return *false*.
        1. If _body_ is an |AsyncConciseBody|, return *false*.
        1. Return the result of HasCallInTailPosition of _body_ with argument _call_.