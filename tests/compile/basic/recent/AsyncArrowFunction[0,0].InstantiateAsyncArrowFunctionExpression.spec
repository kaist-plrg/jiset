        1. If _name_ is not present, set _name_ to *""*.
        1. Let _scope_ be the LexicalEnvironment of the running execution context.
        1. Let _sourceText_ be the source text matched by |AsyncArrowFunction|.
        1. Let _parameters_ be |AsyncArrowBindingIdentifier|.
        1. Let _closure_ be ! OrdinaryFunctionCreate(%AsyncFunction.prototype%, _sourceText_, _parameters_, |AsyncConciseBody|, ~lexical-this~, _scope_).
        1. Perform SetFunctionName(_closure_, _name_).
        1. Return _closure_.