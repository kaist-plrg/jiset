        1. If the function code for |FunctionExpression| is strict mode code, let _strict_ be *true*. Otherwise let _strict_ be *false*.
        1. Let _scope_ be the LexicalEnvironment of the running execution context.
        1. Let _closure_ be FunctionCreate(~Normal~, |FormalParameters|, |FunctionBody|, _scope_, _strict_).
        1. Perform MakeConstructor(_closure_).
        1. Return _closure_.