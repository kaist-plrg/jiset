        1. Let _propKey_ be the result of evaluating |PropertyName|.
        1. ReturnIfAbrupt(_propKey_).
        1. Let _scope_ be the running execution context's LexicalEnvironment.
        1. Let _formalParameterList_ be an instance of the production <emu-grammar>FormalParameters : [empty]</emu-grammar>.
        1. Let _closure_ be OrdinaryFunctionCreate(%Function.prototype%, _formalParameterList_, |FunctionBody|, ~non-lexical-this~, _scope_).
        1. Perform MakeMethod(_closure_, _object_).
        1. Perform SetFunctionName(_closure_, _propKey_, *"get"*).
        1. Set _closure_.[[SourceText]] to the source text matched by |MethodDefinition|.
        1. Let _desc_ be the PropertyDescriptor { [[Get]]: _closure_, [[Enumerable]]: _enumerable_, [[Configurable]]: *true* }.
        1. Return ? DefinePropertyOrThrow(_object_, _propKey_, _desc_).