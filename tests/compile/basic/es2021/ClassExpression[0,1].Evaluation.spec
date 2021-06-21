        1. Let _className_ be StringValue of |BindingIdentifier|.
        1. Let _value_ be ? ClassDefinitionEvaluation of |ClassTail| with arguments _className_ and _className_.
        1. Set _value_.[[SourceText]] to the source text matched by |ClassExpression|.
        1. Return _value_.