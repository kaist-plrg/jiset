        1. Let _scope_ be the LexicalEnvironment of the running execution context.
        1. Let _closure_ be OrdinaryFunctionCreate(%Generator%, |FormalParameters|, |GeneratorBody|, ~non-lexical-this~, _scope_).
        1. Let _prototype_ be OrdinaryObjectCreate(%Generator.prototype%).
        1. Perform DefinePropertyOrThrow(_closure_, *"prototype"*, PropertyDescriptor { [[Value]]: _prototype_, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *false* }).
        1. Set _closure_.[[SourceText]] to the source text matched by |GeneratorExpression|.
        1. Return _closure_.