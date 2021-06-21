          1. Let _rhs_ be the result of evaluating |Initializer|.
          1. Let _value_ be ? GetValue(_rhs_).
          1. Let _env_ be the running execution context's LexicalEnvironment.
          1. Return the result of performing BindingInitialization for |BindingPattern| using _value_ and _env_ as the arguments.