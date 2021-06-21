        1. Let _oldEnv_ be the running execution context's LexicalEnvironment.
        1. Let _blockEnv_ be NewDeclarativeEnvironment(_oldEnv_).
        1. Perform BlockDeclarationInstantiation(|StatementList|, _blockEnv_).
        1. Set the running execution context's LexicalEnvironment to _blockEnv_.
        1. Let _blockValue_ be the result of evaluating |StatementList|.
        1. Set the running execution context's LexicalEnvironment to _oldEnv_.
        1. Return _blockValue_.