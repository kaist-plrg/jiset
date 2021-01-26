          1. If _lex_ is the value *null*, then
            1. Return a value of type Reference whose base value component is *undefined*, whose referenced name component is _name_, and whose strict reference flag is _strict_.
          1. Let _envRec_ be _lex_'s EnvironmentRecord.
          1. Let _exists_ be ? _envRec_.HasBinding(_name_).
          1. If _exists_ is *true*, then
            1. Return a value of type Reference whose base value component is _envRec_, whose referenced name component is _name_, and whose strict reference flag is _strict_.
          1. Else,
            1. Let _outer_ be the value of _lex_'s outer environment reference.
            1. Return ? GetIdentifierReference(_outer_, _name_, _strict_).