          1. Let _env_ be a new Lexical Environment.
          1. Let _objRec_ be a new object Environment Record containing _G_ as the binding object.
          1. Let _dclRec_ be a new declarative Environment Record containing no bindings.
          1. Let _globalRec_ be a new global Environment Record.
          1. Set _globalRec_.[[ObjectRecord]] to _objRec_.
          1. Set _globalRec_.[[GlobalThisValue]] to _thisValue_.
          1. Set _globalRec_.[[DeclarativeRecord]] to _dclRec_.
          1. Set _globalRec_.[[VarNames]] to a new empty List.
          1. Set _env_'s EnvironmentRecord to _globalRec_.
          1. Set the outer lexical environment reference of _env_ to *null*.
          1. Return _env_.