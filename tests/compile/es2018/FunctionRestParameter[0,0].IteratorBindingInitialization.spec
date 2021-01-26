        1. If ContainsExpression of |BindingRestElement| is *false*, return the result of performing IteratorBindingInitialization for |BindingRestElement| using _iteratorRecord_ and _environment_ as the arguments.
        1. Let _currentContext_ be the running execution context.
        1. Let _originalEnv_ be the VariableEnvironment of _currentContext_.
        1. Assert: The VariableEnvironment and LexicalEnvironment of _currentContext_ are the same.
        1. Assert: _environment_ and _originalEnv_ are the same.
        1. Let _paramVarEnv_ be NewDeclarativeEnvironment(_originalEnv_).
        1. Set the VariableEnvironment of _currentContext_ to _paramVarEnv_.
        1. Set the LexicalEnvironment of _currentContext_ to _paramVarEnv_.
        1. Let _result_ be the result of performing IteratorBindingInitialization for |BindingRestElement| using _iteratorRecord_ and _environment_ as the arguments.
        1. Set the VariableEnvironment of _currentContext_ to _originalEnv_.
        1. Set the LexicalEnvironment of _currentContext_ to _originalEnv_.
        1. Return _result_.