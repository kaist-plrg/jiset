          1. Let _env_ be a new Lexical Environment.
          1. Let _envRec_ be a new declarative Environment Record containing no bindings.
          1. Set _env_'s EnvironmentRecord to _envRec_.
          1. Set the outer lexical environment reference of _env_ to _E_.
          1. Return _env_.