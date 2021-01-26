        1. If the function code for |AsyncGeneratorDeclaration| is strict mode code, let _strict_ be *true*. Otherwise let _strict_ be *false*.
        1. Let _F_ be AsyncGeneratorFunctionCreate(~Normal~, |FormalParameters|, |AsyncGeneratorBody|, _scope_, _strict_).
        1. Let _prototype_ be ObjectCreate(%AsyncGeneratorPrototype%).
        1. Perform DefinePropertyOrThrow(_F_, `"prototype"`, PropertyDescriptor { [[Value]]: _prototype_, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *false* }).
        1. Perform SetFunctionName(_F_, `"default"`).
        1. Set _F_.[[SourceText]] to the source text matched by |AsyncGeneratorDeclaration|.
        1. Return _F_.