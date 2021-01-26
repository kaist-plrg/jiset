        1. If the function code for |GeneratorDeclaration| is strict mode code, let _strict_ be *true*. Otherwise let _strict_ be *false*.
        1. Let _name_ be StringValue of |BindingIdentifier|.
        1. Let _F_ be GeneratorFunctionCreate(~Normal~, |FormalParameters|, |GeneratorBody|, _scope_, _strict_).
        1. Let _prototype_ be ObjectCreate(%GeneratorPrototype%).
        1. Perform DefinePropertyOrThrow(_F_, `"prototype"`, PropertyDescriptor{[[Value]]: _prototype_, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *false*}).
        1. Perform SetFunctionName(_F_, _name_).
        1. Return _F_.