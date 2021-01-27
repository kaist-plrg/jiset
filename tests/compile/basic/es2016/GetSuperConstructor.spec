          1. Let _envRec_ be GetThisEnvironment( ).
          1. Assert: _envRec_ is a function Environment Record.
          1. Let _activeFunction_ be _envRec_.[[FunctionObject]].
          1. Let _superConstructor_ be ? _activeFunction_.[[GetPrototypeOf]]().
          1. If IsConstructor(_superConstructor_) is *false*, throw a *TypeError* exception.
          1. Return _superConstructor_.