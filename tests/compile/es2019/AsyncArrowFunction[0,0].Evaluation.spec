        1. If the function code for this |AsyncArrowFunction| is strict mode code, let _strict_ be *true*. Otherwise, let _strict_ be *false*.
        1. Let _scope_ be the LexicalEnvironment of the running execution context.
        1. Let _parameters_ be |AsyncArrowBindingIdentifier|.
        1. Let _closure_ be ! AsyncFunctionCreate(~Arrow~, _parameters_, |AsyncConciseBody|, _scope_, _strict_).
        1. Return _closure_.