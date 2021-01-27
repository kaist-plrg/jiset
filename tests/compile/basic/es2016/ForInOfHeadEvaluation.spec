          1. Let _oldEnv_ be the running execution context's LexicalEnvironment.
          1. If _TDZnames_ is not an empty List, then
            1. Assert: _TDZnames_ has no duplicate entries.
            1. Let _TDZ_ be NewDeclarativeEnvironment(_oldEnv_).
            1. Let _TDZEnvRec_ be _TDZ_'s EnvironmentRecord.
            1. For each string _name_ in _TDZnames_, do
              1. Perform ! _TDZEnvRec_.CreateMutableBinding(_name_, *false*).
            1. Set the running execution context's LexicalEnvironment to _TDZ_.
          1. Let _exprRef_ be the result of evaluating _expr_.
          1. Set the running execution context's LexicalEnvironment to _oldEnv_.
          1. Let _exprValue_ be ? GetValue(_exprRef_).
          1. If _iterationKind_ is ~enumerate~, then
            1. If _exprValue_.[[Value]] is *null* or *undefined*, then
              1. Return Completion{[[Type]]: ~break~, [[Value]]: ~empty~, [[Target]]: ~empty~}.
            1. Let _obj_ be ToObject(_exprValue_).
            1. Return ? EnumerateObjectProperties(_obj_).
          1. Else,
            1. Assert: _iterationKind_ is ~iterate~.
            1. Return ? GetIterator(_exprValue_).