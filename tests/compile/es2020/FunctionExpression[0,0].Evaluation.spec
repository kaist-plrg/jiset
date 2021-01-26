        1. Let _scope_ be the LexicalEnvironment of the running execution context.
        1. Let _closure_ be OrdinaryFunctionCreate(%Function.prototype%, |FormalParameters|, |FunctionBody|, ~non-lexical-this~, _scope_).
        1. Perform MakeConstructor(_closure_).
        1. Set _closure_.[[SourceText]] to the source text matched by |FunctionExpression|.
        1. Return _closure_.