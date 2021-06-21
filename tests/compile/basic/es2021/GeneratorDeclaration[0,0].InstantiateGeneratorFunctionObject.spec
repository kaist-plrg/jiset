        1. Let _name_ be StringValue of |BindingIdentifier|.
        1. Let _sourceText_ be the source text matched by |GeneratorDeclaration|.
        1. Let _F_ be OrdinaryFunctionCreate(%GeneratorFunction.prototype%, _sourceText_, |FormalParameters|, |GeneratorBody|, ~non-lexical-this~, _scope_).
        1. Perform SetFunctionName(_F_, _name_).
        1. Let _prototype_ be ! OrdinaryObjectCreate(%GeneratorFunction.prototype.prototype%).
        1. Perform DefinePropertyOrThrow(_F_, *"prototype"*, PropertyDescriptor { [[Value]]: _prototype_, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *false* }).
        1. Return _F_.