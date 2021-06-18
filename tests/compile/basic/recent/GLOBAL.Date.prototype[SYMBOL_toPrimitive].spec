          1. Let _O_ be the *this* value.
          1. If Type(_O_) is not Object, throw a *TypeError* exception.
          1. If _hint_ is *"string"* or *"default"*, then
            1. Let _tryFirst_ be ~string~.
          1. Else if _hint_ is *"number"*, then
            1. Let _tryFirst_ be ~number~.
          1. Else, throw a *TypeError* exception.
          1. Return ? OrdinaryToPrimitive(_O_, _tryFirst_).