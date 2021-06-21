        1. If _value_ is not present, then
          1. Let _result_ be ? Call(_iteratorRecord_.[[NextMethod]], _iteratorRecord_.[[Iterator]]).
        1. Else,
          1. Let _result_ be ? Call(_iteratorRecord_.[[NextMethod]], _iteratorRecord_.[[Iterator]], « _value_ »).
        1. If Type(_result_) is not Object, throw a *TypeError* exception.
        1. Return _result_.