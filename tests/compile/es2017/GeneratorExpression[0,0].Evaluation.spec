        1. If the function code for this |GeneratorExpression| is strict mode code, let _strict_ be *true*. Otherwise let _strict_ be *false*.
        1. Let _scope_ be the LexicalEnvironment of the running execution context.
        1. Let _closure_ be GeneratorFunctionCreate(~Normal~, |FormalParameters|, |GeneratorBody|, _scope_, _strict_).
        1. Let _prototype_ be ObjectCreate(%GeneratorPrototype%).
        1. Perform DefinePropertyOrThrow(_closure_, `"prototype"`, PropertyDescriptor{[[Value]]: _prototype_, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *false*}).
        1. Return _closure_.