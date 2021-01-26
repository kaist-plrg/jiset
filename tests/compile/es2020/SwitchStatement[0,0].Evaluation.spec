        1. Let _exprRef_ be the result of evaluating |Expression|.
        1. Let _switchValue_ be ? GetValue(_exprRef_).
        1. Let _oldEnv_ be the running execution context's LexicalEnvironment.
        1. Let _blockEnv_ be NewDeclarativeEnvironment(_oldEnv_).
        1. Perform BlockDeclarationInstantiation(|CaseBlock|, _blockEnv_).
        1. Set the running execution context's LexicalEnvironment to _blockEnv_.
        1. Let _R_ be CaseBlockEvaluation of |CaseBlock| with argument _switchValue_.
        1. Set the running execution context's LexicalEnvironment to _oldEnv_.
        1. Return _R_.