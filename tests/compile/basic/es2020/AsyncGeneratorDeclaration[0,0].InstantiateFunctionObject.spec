        1. Let _name_ be StringValue of |BindingIdentifier|.
        1. Let _F_ be ! OrdinaryFunctionCreate(%AsyncGenerator%, |FormalParameters|, |AsyncGeneratorBody|, ~non-lexical-this~, _scope_).
        1. Let _prototype_ be ! OrdinaryObjectCreate(%AsyncGenerator.prototype%).
        1. Perform ! DefinePropertyOrThrow(_F_, *"prototype"*, PropertyDescriptor { [[Value]]: _prototype_, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *false* }).
        1. Perform ! SetFunctionName(_F_, _name_).
        1. Set _F_.[[SourceText]] to the source text matched by |AsyncGeneratorDeclaration|.
        1. Return _F_.