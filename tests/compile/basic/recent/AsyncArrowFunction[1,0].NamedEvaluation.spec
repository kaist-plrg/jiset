        1. Let _scope_ be the LexicalEnvironment of the running execution context.
        1. Let _sourceText_ be the source text matched by |AsyncArrowFunction|.
        1. Let _head_ be CoveredAsyncArrowHead of |CoverCallExpressionAndAsyncArrowHead|.
        1. Let _parameters_ be the |ArrowFormalParameters| of _head_.
        1. Let _closure_ be ! OrdinaryFunctionCreate(%AsyncFunction.prototype%, _sourceText_, _parameters_, |AsyncConciseBody|, ~lexical-this~, _scope_).
        1. Perform SetFunctionName(_closure_, _name_).
        1. Return _closure_.