          1. Assert: Either Type(_V_) is Object or Type(_V_) is Null.
          1. Let _current_ be the value of the [[Prototype]] internal slot of _O_.
          1. If SameValue(_V_, _current_) is *true*, return *true*.
          1. Return *false*.