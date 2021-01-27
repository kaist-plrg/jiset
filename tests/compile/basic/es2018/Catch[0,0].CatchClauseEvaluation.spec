        1. Let _oldEnv_ be the running execution context's LexicalEnvironment.
        1. Let _catchEnv_ be NewDeclarativeEnvironment(_oldEnv_).
        1. Let _catchEnvRec_ be _catchEnv_'s EnvironmentRecord.
        1. For each element _argName_ of the BoundNames of |CatchParameter|, do
          1. Perform ! _catchEnvRec_.CreateMutableBinding(_argName_, *false*).
        1. Set the running execution context's LexicalEnvironment to _catchEnv_.
        1. Let _status_ be the result of performing BindingInitialization for |CatchParameter| passing _thrownValue_ and _catchEnv_ as arguments.
        1. If _status_ is an abrupt completion, then
          1. Set the running execution context's LexicalEnvironment to _oldEnv_.
          1. Return Completion(_status_).
        1. Let _B_ be the result of evaluating |Block|.
        1. Set the running execution context's LexicalEnvironment to _oldEnv_.
        1. Return Completion(_B_).