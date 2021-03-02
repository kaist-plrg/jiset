        1. If the function code for this |ArrowFunction| is strict mode code, let _strict_ be *true*. Otherwise let _strict_ be *false*.
        1. Let _scope_ be the LexicalEnvironment of the running execution context.
        1. Let _parameters_ be CoveredFormalsList of |ArrowParameters|.
        1. Let _closure_ be FunctionCreate(~Arrow~, _parameters_, |ConciseBody|, _scope_, _strict_).
        1. Set _closure_.[[SourceText]] to the source text matched by |ArrowFunction|.
        1. Return _closure_.