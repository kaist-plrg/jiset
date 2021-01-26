        1. Let _propKey_ be the result of evaluating |PropertyName|.
        1. ReturnIfAbrupt(_propKey_).
        1. If the function code for this |MethodDefinition| is strict mode code, let _strict_ be *true*. Otherwise let _strict_ be *false*.
        1. Let _scope_ be the running execution context's LexicalEnvironment.
        1. Let _formalParameterList_ be the production <emu-grammar>FormalParameters : [empty]</emu-grammar>.
        1. Let _closure_ be FunctionCreate(~Method~, _formalParameterList_, |FunctionBody|, _scope_, _strict_).
        1. Perform MakeMethod(_closure_, _object_).
        1. Perform SetFunctionName(_closure_, _propKey_, `"get"`).
        1. Let _desc_ be the PropertyDescriptor{[[Get]]: _closure_, [[Enumerable]]: _enumerable_, [[Configurable]]: *true*}.
        1. Return ? DefinePropertyOrThrow(_object_, _propKey_, _desc_).