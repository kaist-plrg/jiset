        1. If |BindingIdentifier_opt| is not present, let _className_ be *undefined*.
        1. Else, let _className_ be StringValue of |BindingIdentifier|.
        1. Let _value_ be the result of ClassDefinitionEvaluation of |ClassTail| with argument _className_.
        1. ReturnIfAbrupt(_value_).
        1. If _className_ is not *undefined*, then
          1. Let _hasNameProperty_ be ? HasOwnProperty(_value_, `"name"`).
          1. If _hasNameProperty_ is *false*, then
            1. Perform SetFunctionName(_value_, _className_).
        1. Return NormalCompletion(_value_).