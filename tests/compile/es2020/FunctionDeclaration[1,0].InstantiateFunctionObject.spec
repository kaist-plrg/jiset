        1. Let _F_ be OrdinaryFunctionCreate(%Function.prototype%, |FormalParameters|, |FunctionBody|, ~non-lexical-this~, _scope_).
        1. Perform MakeConstructor(_F_).
        1. Perform SetFunctionName(_F_, *"default"*).
        1. Set _F_.[[SourceText]] to the source text matched by |FunctionDeclaration|.
        1. Return _F_.