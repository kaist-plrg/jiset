          1. Let _thisMode_ be _F_.[[ThisMode]].
          1. If _thisMode_ is ~lexical~, return NormalCompletion(*undefined*).
          1. Let _calleeRealm_ be _F_.[[Realm]].
          1. Let _localEnv_ be the LexicalEnvironment of _calleeContext_.
          1. If _thisMode_ is ~strict~, let _thisValue_ be _thisArgument_.
          1. Else,
            1. If _thisArgument_ is *undefined* or *null*, then
              1. Let _globalEnv_ be _calleeRealm_.[[GlobalEnv]].
              1. Assert: _globalEnv_ is a global Environment Record.
              1. Let _thisValue_ be _globalEnv_.[[GlobalThisValue]].
            1. Else,
              1. Let _thisValue_ be ! ToObject(_thisArgument_).
              1. NOTE: ToObject produces wrapper objects using _calleeRealm_.
          1. Assert: _localEnv_ is a function Environment Record.
          1. Assert: The next step never returns an abrupt completion because _localEnv_.[[ThisBindingStatus]] is not ~initialized~.
          1. Return _localEnv_.BindThisValue(_thisValue_).