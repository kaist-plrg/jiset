        1. Let _propKey_ be the result of evaluating |PropertyName|.
        1. ReturnIfAbrupt(_propKey_).
        1. Let _scope_ be the running execution context's LexicalEnvironment.
        1. If _functionPrototype_ is present as a parameter, then
          1. Let _prototype_ be _functionPrototype_.
        1. Else,
          1. Let _prototype_ be %Function.prototype%.
        1. Let _closure_ be OrdinaryFunctionCreate(_prototype_, |UniqueFormalParameters|, |FunctionBody|, ~non-lexical-this~, _scope_).
        1. Perform MakeMethod(_closure_, _object_).
        1. Set _closure_.[[SourceText]] to the source text matched by |MethodDefinition|.
        1. Return the Record { [[Key]]: _propKey_, [[Closure]]: _closure_ }.