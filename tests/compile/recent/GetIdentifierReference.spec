          1. If _env_ is the value *null*, then
            1. Return the Reference Record { [[Base]]: ~unresolvable~, [[ReferencedName]]: _name_, [[Strict]]: _strict_, [[ThisValue]]: ~empty~ }.
          1. Let _exists_ be ? _env_.HasBinding(_name_).
          1. If _exists_ is *true*, then
            1. Return the Reference Record { [[Base]]: _env_, [[ReferencedName]]: _name_, [[Strict]]: _strict_, [[ThisValue]]: ~empty~ }.
          1. Else,
            1. Let _outer_ be _env_.[[OuterEnv]].
            1. Return ? GetIdentifierReference(_outer_, _name_, _strict_).