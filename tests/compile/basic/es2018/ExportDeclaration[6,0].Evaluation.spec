          1. Let _value_ be the result of BindingClassDeclarationEvaluation of |ClassDeclaration|.
          1. ReturnIfAbrupt(_value_).
          1. Let _className_ be the sole element of BoundNames of |ClassDeclaration|.
          1. If _className_ is `"*default*"`, then
            1. Let _hasNameProperty_ be ? HasOwnProperty(_value_, `"name"`).
            1. If _hasNameProperty_ is *false*, perform SetFunctionName(_value_, `"default"`).
            1. Let _env_ be the running execution context's LexicalEnvironment.
            1. Perform ? InitializeBoundName(`"*default*"`, _value_, _env_).
          1. Return NormalCompletion(~empty~).