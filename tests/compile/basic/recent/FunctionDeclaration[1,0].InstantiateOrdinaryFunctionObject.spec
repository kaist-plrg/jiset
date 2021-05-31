        1. Let _sourceText_ be the source text matched by |FunctionDeclaration|.
        1. Let _F_ be OrdinaryFunctionCreate(%Function.prototype%, _sourceText_, |FormalParameters|, |FunctionBody|, ~non-lexical-this~, _scope_).
        1. Perform SetFunctionName(_F_, *"default"*).
        1. Perform MakeConstructor(_F_).
        1. Return _F_.