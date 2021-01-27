          1. If Type(_O_) is not Object, return _O_.
          1. Let _status_ be ? SetIntegrityLevel(_O_, `"frozen"`).
          1. If _status_ is *false*, throw a *TypeError* exception.
          1. Return _O_.