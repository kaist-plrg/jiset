        1. Let _F_ be GeneratorFunctionCreate(~Normal~, |FormalParameters|, |GeneratorBody|, _scope_, *true*).
        1. Let _prototype_ be ObjectCreate(%GeneratorPrototype%).
        1. Perform DefinePropertyOrThrow(_F_, `"prototype"`, PropertyDescriptor { [[Value]]: _prototype_, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *false* }).
        1. Perform SetFunctionName(_F_, `"default"`).
        1. Set _F_.[[SourceText]] to the source text matched by |GeneratorDeclaration|.
        1. Return _F_.