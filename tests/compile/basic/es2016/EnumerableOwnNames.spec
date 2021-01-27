        1. Assert: Type(_O_) is Object.
        1. Let _ownKeys_ be ? _O_.[[OwnPropertyKeys]]().
        1. Let _names_ be a new empty List.
        1. Repeat, for each element _key_ of _ownKeys_ in List order
          1. If Type(_key_) is String, then
            1. Let _desc_ be ? _O_.[[GetOwnProperty]](_key_).
            1. If _desc_ is not *undefined*, then
              1. If _desc_.[[Enumerable]] is *true*, append _key_ to _names_.
        1. Order the elements of _names_ so they are in the same relative order as would be produced by the Iterator that would be returned if the EnumerateObjectProperties internal method was invoked with _O_.
        1. Return _names_.