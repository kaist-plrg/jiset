        1. If _value_ was not passed, then
          1. Let _result_ be ? Invoke(_iterator_, `"next"`, « »).
        1. Else,
          1. Let _result_ be ? Invoke(_iterator_, `"next"`, « _value_ »).
        1. If Type(_result_) is not Object, throw a *TypeError* exception.
        1. Return _result_.