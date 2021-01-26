        1. Let _className_ be StringValue of |BindingIdentifier|.
        1. Let _value_ be the result of ClassDefinitionEvaluation of |ClassTail| with argument _className_.
        1. ReturnIfAbrupt(_value_).
        1. Let _hasNameProperty_ be ? HasOwnProperty(_value_, `"name"`).
        1. If _hasNameProperty_ is *false*, perform SetFunctionName(_value_, _className_).
        1. Let _env_ be the running execution context's LexicalEnvironment.
        1. Perform ? InitializeBoundName(_className_, _value_, _env_).
        1. Return _value_.