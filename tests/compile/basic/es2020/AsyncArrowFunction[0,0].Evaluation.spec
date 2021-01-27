        1. Let _scope_ be the LexicalEnvironment of the running execution context.
        1. Let _parameters_ be |AsyncArrowBindingIdentifier|.
        1. Let _closure_ be ! OrdinaryFunctionCreate(%AsyncFunction.prototype%, _parameters_, |AsyncConciseBody|, ~lexical-this~, _scope_).
        1. Set _closure_.[[SourceText]] to the source text matched by |AsyncArrowFunction|.
        1. Return _closure_.