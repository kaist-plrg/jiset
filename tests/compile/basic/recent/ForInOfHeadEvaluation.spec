          1. Let _oldEnv_ be the running execution context's LexicalEnvironment.
          1. If _uninitializedBoundNames_ is not an empty List, then
            1. Assert: _uninitializedBoundNames_ has no duplicate entries.
            1. Let _newEnv_ be NewDeclarativeEnvironment(_oldEnv_).
            1. For each String _name_ of _uninitializedBoundNames_, do
              1. Perform ! _newEnv_.CreateMutableBinding(_name_, *false*).
            1. Set the running execution context's LexicalEnvironment to _newEnv_.
          1. Let _exprRef_ be the result of evaluating _expr_.
          1. Set the running execution context's LexicalEnvironment to _oldEnv_.
          1. Let _exprValue_ be ? GetValue(_exprRef_).
          1. If _iterationKind_ is ~enumerate~, then
            1. If _exprValue_ is *undefined* or *null*, then
              1. Return Completion { [[Type]]: ~break~, [[Value]]: ~empty~, [[Target]]: ~empty~ }.
            1. Let _obj_ be ! ToObject(_exprValue_).
            1. Let _iterator_ be ? EnumerateObjectProperties(_obj_).
            1. Let _nextMethod_ be ! GetV(_iterator_, *"next"*).
            1. Return the Record { [[Iterator]]: _iterator_, [[NextMethod]]: _nextMethod_, [[Done]]: *false* }.
          1. Else,
            1. Assert: _iterationKind_ is ~iterate~ or ~async-iterate~.
            1. If _iterationKind_ is ~async-iterate~, let _iteratorHint_ be ~async~.
            1. Else, let _iteratorHint_ be ~sync~.
            1. Return ? GetIterator(_exprValue_, _iteratorHint_).