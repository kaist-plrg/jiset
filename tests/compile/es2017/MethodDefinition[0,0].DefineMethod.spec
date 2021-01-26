        1. Let _propKey_ be the result of evaluating |PropertyName|.
        1. ReturnIfAbrupt(_propKey_).
        1. If the function code for this |MethodDefinition| is strict mode code, let _strict_ be *true*. Otherwise let _strict_ be *false*.
        1. Let _scope_ be the running execution context's LexicalEnvironment.
        1. If _functionPrototype_ was passed as a parameter, then
          1. Let _kind_ be ~Normal~.
          1. Let _prototype_ be _functionPrototype_.
        1. Else,
          1. Let _kind_ be ~Method~.
          1. Let _prototype_ be the intrinsic object %FunctionPrototype%.
        1. Let _closure_ be FunctionCreate(_kind_, |UniqueFormalParameters|, |FunctionBody|, _scope_, _strict_, _prototype_).
        1. Perform MakeMethod(_closure_, _object_).
        1. Return the Record{[[Key]]: _propKey_, [[Closure]]: _closure_}.