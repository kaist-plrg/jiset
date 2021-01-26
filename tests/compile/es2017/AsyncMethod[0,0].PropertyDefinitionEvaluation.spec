        1. Let _propKey_ be the result of evaluating |PropertyName|.
        1. ReturnIfAbrupt(_propKey_).
        1. If the function code for this |AsyncMethod| is strict mode code, let _strict_ be *true*. Otherwise let _strict_ be *false*.
        1. Let _scope_ be the LexicalEnvironment of the running execution context.
        1. Let _closure_ be ! AsyncFunctionCreate(~Method~, |UniqueFormalParameters|, |AsyncFunctionBody|, _scope_, _strict_).
        1. Perform ! MakeMethod(_closure_, _object_).
        1. Perform ! SetFunctionName(_closure_, _propKey_).
        1. Let _desc_ be the PropertyDescriptor{[[Value]]: _closure_, [[Writable]]: *true*, [[Enumerable]]: _enumerable_, [[Configurable]]: *true*}.
        1. Return ? DefinePropertyOrThrow(_object_, _propKey_, _desc_).