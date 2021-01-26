          1. Assert: _O_ is an Integer-Indexed exotic object.
          1. Assert: Type(_index_) is Number.
          1. If ! IsInteger(_index_) is *false*, return *false*.
          1. If _index_ is *-0*, return *false*.
          1. If _index_ < 0 or _index_ ≥ _O_.[[ArrayLength]], return *false*.
          1. Return *true*.