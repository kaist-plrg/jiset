          1. If Type(_O_) is neither Object nor Null, throw a *TypeError* exception.
          1. Let _obj_ be ObjectCreate(_O_).
          1. If _Properties_ is not *undefined*, then
            1. Return ? ObjectDefineProperties(_obj_, _Properties_).
          1. Return _obj_.