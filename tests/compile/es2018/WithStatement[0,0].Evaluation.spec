        1. Let _val_ be the result of evaluating |Expression|.
        1. Let _obj_ be ? ToObject(? GetValue(_val_)).
        1. Let _oldEnv_ be the running execution context's LexicalEnvironment.
        1. Let _newEnv_ be NewObjectEnvironment(_obj_, _oldEnv_).
        1. Set the _withEnvironment_ flag of _newEnv_'s EnvironmentRecord to *true*.
        1. Set the running execution context's LexicalEnvironment to _newEnv_.
        1. Let _C_ be the result of evaluating |Statement|.
        1. Set the running execution context's LexicalEnvironment to _oldEnv_.
        1. Return Completion(UpdateEmpty(_C_, *undefined*)).