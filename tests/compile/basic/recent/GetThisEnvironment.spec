        1. Let _env_ be the running execution context's LexicalEnvironment.
        1. [id="step-getthisenvironment-loop"] Repeat,
          1. Let _exists_ be _env_.HasThisBinding().
          1. If _exists_ is *true*, return _env_.
          1. Let _outer_ be _env_.[[OuterEnv]].
          1. Assert: _outer_ is not *null*.
          1. Set _env_ to _outer_.