        1. Let _F_ be ! OrdinaryFunctionCreate(%AsyncFunction.prototype%, |FormalParameters|, |AsyncFunctionBody|, ~non-lexical-this~, _scope_).
        1. Perform ! SetFunctionName(_F_, *"default"*).
        1. Set _F_.[[SourceText]] to the source text matched by |AsyncFunctionDeclaration|.
        1. Return _F_.