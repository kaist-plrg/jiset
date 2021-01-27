        1. Let _className_ be StringValue of |BindingIdentifier|.
        1. Let _value_ be the result of ClassDefinitionEvaluation of |ClassTail| with arguments _className_ and _className_.
        1. ReturnIfAbrupt(_value_).
        1. Set _value_.[[SourceText]] to the source text matched by |ClassDeclaration|.
        1. Let _env_ be the running execution context's LexicalEnvironment.
        1. Perform ? InitializeBoundName(_className_, _value_, _env_).
        1. Return _value_.