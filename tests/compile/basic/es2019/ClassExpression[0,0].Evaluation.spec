        1. If |BindingIdentifier_opt| is not present, let _className_ be *undefined*.
        1. Else, let _className_ be StringValue of |BindingIdentifier|.
        1. Let _value_ be the result of ClassDefinitionEvaluation of |ClassTail| with arguments _className_ and _className_.
        1. ReturnIfAbrupt(_value_).
        1. Set _value_.[[SourceText]] to the source text matched by |ClassExpression|.
        1. Return _value_.