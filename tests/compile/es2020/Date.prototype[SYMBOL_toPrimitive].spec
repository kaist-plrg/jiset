          1. Let _O_ be the *this* value.
          1. If Type(_O_) is not Object, throw a *TypeError* exception.
          1. If _hint_ is the String value *"string"* or the String value *"default"*, then
            1. Let _tryFirst_ be *"string"*.
          1. Else if _hint_ is the String value *"number"*, then
            1. Let _tryFirst_ be *"number"*.
          1. Else, throw a *TypeError* exception.
          1. Return ? OrdinaryToPrimitive(_O_, _tryFirst_).