          1. Let _O_ be ? RequireObjectCoercible(_O_).
          1. If Type(_proto_) is neither Object nor Null, throw a *TypeError* exception.
          1. If Type(_O_) is not Object, return _O_.
          1. Let _status_ be ? _O_.[[SetPrototypeOf]](_proto_).
          1. If _status_ is *false*, throw a *TypeError* exception.
          1. Return _O_.