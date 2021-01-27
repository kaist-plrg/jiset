        1. If _method_ was not passed, then
          1. Set _method_ to ? GetMethod(_obj_, @@iterator).
        1. Let _iterator_ be ? Call(_method_, _obj_).
        1. If Type(_iterator_) is not Object, throw a *TypeError* exception.
        1. Return _iterator_.