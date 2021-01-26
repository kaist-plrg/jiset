          1. Let _envRec_ be _environment_'s EnvironmentRecord.
          1. Assert: _envRec_ is a declarative Environment Record.
          1. For each element _name_ of the BoundNames of |ForBinding| do
            1. If IsConstantDeclaration of |LetOrConst| is *true*, then
              1. Perform ! _envRec_.CreateImmutableBinding(_name_, *true*).
            1. Else,
              1. Perform ! _envRec_.CreateMutableBinding(_name_, *false*).