        1. Let _scope_ be the LexicalEnvironment of the running execution context.
        1. Let _parameters_ be CoveredFormalsList of |ArrowParameters|.
        1. Let _closure_ be OrdinaryFunctionCreate(%Function.prototype%, _parameters_, |ConciseBody|, ~lexical-this~, _scope_).
        1. Set _closure_.[[SourceText]] to the source text matched by |ArrowFunction|.
        1. Return _closure_.