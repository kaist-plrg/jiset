          1. Let _envRec_ be GetThisEnvironment().
          1. Assert: _envRec_ is a function Environment Record.
          1. Let _activeFunction_ be _envRec_.[[FunctionObject]].
          1. Assert: _activeFunction_ is an ECMAScript function object.
          1. Let _superConstructor_ be ! _activeFunction_.[[GetPrototypeOf]]().
          1. Return _superConstructor_.