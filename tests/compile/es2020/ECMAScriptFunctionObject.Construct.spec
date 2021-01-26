        1. Assert: _F_ is an ECMAScript function object.
        1. Assert: Type(_newTarget_) is Object.
        1. Let _callerContext_ be the running execution context.
        1. Let _kind_ be _F_.[[ConstructorKind]].
        1. If _kind_ is ~base~, then
          1. Let _thisArgument_ be ? OrdinaryCreateFromConstructor(_newTarget_, *"%Object.prototype%"*).
        1. Let _calleeContext_ be PrepareForOrdinaryCall(_F_, _newTarget_).
        1. Assert: _calleeContext_ is now the running execution context.
        1. If _kind_ is ~base~, perform OrdinaryCallBindThis(_F_, _calleeContext_, _thisArgument_).
        1. Let _constructorEnv_ be the LexicalEnvironment of _calleeContext_.
        1. Let _envRec_ be _constructorEnv_'s EnvironmentRecord.
        1. Let _result_ be OrdinaryCallEvaluateBody(_F_, _argumentsList_).
        1. Remove _calleeContext_ from the execution context stack and restore _callerContext_ as the running execution context.
        1. If _result_.[[Type]] is ~return~, then
          1. If Type(_result_.[[Value]]) is Object, return NormalCompletion(_result_.[[Value]]).
          1. If _kind_ is ~base~, return NormalCompletion(_thisArgument_).
          1. If _result_.[[Value]] is not *undefined*, throw a *TypeError* exception.
        1. Else, ReturnIfAbrupt(_result_).
        1. Return ? _envRec_.GetThisBinding().