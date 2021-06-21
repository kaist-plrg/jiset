        1. Assert: Type(_O_) is Object.
        1. Let _ownKeys_ be ? _O_.[[OwnPropertyKeys]]().
        1. Let _properties_ be a new empty List.
        1. For each element _key_ of _ownKeys_, do
          1. If Type(_key_) is String, then
            1. Let _desc_ be ? _O_.[[GetOwnProperty]](_key_).
            1. If _desc_ is not *undefined* and _desc_.[[Enumerable]] is *true*, then
              1. If _kind_ is ~key~, append _key_ to _properties_.
              1. Else,
                1. Let _value_ be ? Get(_O_, _key_).
                1. If _kind_ is ~value~, append _value_ to _properties_.
                1. Else,
                  1. Assert: _kind_ is ~key+value~.
                  1. Let _entry_ be ! CreateArrayFromList(« _key_, _value_ »).
                  1. Append _entry_ to _properties_.
        1. Return _properties_.