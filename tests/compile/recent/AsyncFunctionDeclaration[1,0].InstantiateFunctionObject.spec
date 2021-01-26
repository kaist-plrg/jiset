        1. Let _sourceText_ be the source text matched by |AsyncFunctionDeclaration|.
        1. Let _F_ be ! OrdinaryFunctionCreate(%AsyncFunction.prototype%, _sourceText_, |FormalParameters|, |AsyncFunctionBody|, ~non-lexical-this~, _scope_).
        1. Perform ! SetFunctionName(_F_, *"default"*).
        1. Return _F_.