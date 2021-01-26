        1. Let _value_ be the result of ClassDefinitionEvaluation of |ClassTail| with arguments *undefined* and _name_.
        1. ReturnIfAbrupt(_value_).
        1. Set _value_.[[SourceText]] to the source text matched by |ClassExpression|.
        1. Return _value_.