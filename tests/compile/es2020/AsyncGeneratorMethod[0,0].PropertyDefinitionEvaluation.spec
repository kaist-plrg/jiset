        1. Let _propKey_ be the result of evaluating |PropertyName|.
        1. ReturnIfAbrupt(_propKey_).
        1. Let _scope_ be the running execution context's LexicalEnvironment.
        1. Let _closure_ be ! OrdinaryFunctionCreate(%AsyncGenerator%, |UniqueFormalParameters|, |AsyncGeneratorBody|, ~non-lexical-this~, _scope_).
        1. Perform ! MakeMethod(_closure_, _object_).
        1. Let _prototype_ be ! OrdinaryObjectCreate(%AsyncGenerator.prototype%).
        1. Perform ! DefinePropertyOrThrow(_closure_, *"prototype"*, PropertyDescriptor { [[Value]]: _prototype_, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *false* }).
        1. Perform ! SetFunctionName(_closure_, _propKey_).
        1. Set _closure_.[[SourceText]] to the source text matched by |AsyncGeneratorMethod|.
        1. Let _desc_ be PropertyDescriptor { [[Value]]: _closure_, [[Writable]]: *true*, [[Enumerable]]: _enumerable_, [[Configurable]]: *true* }.
        1. Return ? DefinePropertyOrThrow(_object_, _propKey_, _desc_).