        1. If _name_ is not present, set _name_ to *""*.
        1. Let _scope_ be the LexicalEnvironment of the running execution context.
        1. Let _sourceText_ be the source text matched by |AsyncGeneratorExpression|.
        1. Let _closure_ be ! OrdinaryFunctionCreate(%AsyncGeneratorFunction.prototype%, _sourceText_, |FormalParameters|, |AsyncGeneratorBody|, ~non-lexical-this~, _scope_).
        1. Perform SetFunctionName(_closure_, _name_).
        1. Let _prototype_ be ! OrdinaryObjectCreate(%AsyncGeneratorFunction.prototype.prototype%).
        1. Perform ! DefinePropertyOrThrow(_closure_, *"prototype"*, PropertyDescriptor { [[Value]]: _prototype_, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *false* }).
        1. Return _closure_.