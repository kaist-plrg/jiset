          1. Assert: Either Type(_V_) is Object or Type(_V_) is Null.
          1. Let _current_ be ? _O_.[[GetPrototypeOf]]().
          1. If SameValue(_V_, _current_) is *true*, return *true*.
          1. Return *false*.