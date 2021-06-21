          1. If IsCallable(_adder_) is *false*, throw a *TypeError* exception.
          1. Assert: _iterable_ is present, and is neither *undefined* nor *null*.
          1. Let _iteratorRecord_ be ? GetIterator(_iterable_).
          1. Repeat,
            1. Let _next_ be ? IteratorStep(_iteratorRecord_).
            1. If _next_ is *false*, return _target_.
            1. Let _nextItem_ be ? IteratorValue(_next_).
            1. If Type(_nextItem_) is not Object, then
              1. Let _error_ be ThrowCompletion(a newly created *TypeError* object).
              1. Return ? IteratorClose(_iteratorRecord_, _error_).
            1. Let _k_ be Get(_nextItem_, *"0"*).
            1. If _k_ is an abrupt completion, return ? IteratorClose(_iteratorRecord_, _k_).
            1. Let _v_ be Get(_nextItem_, *"1"*).
            1. If _v_ is an abrupt completion, return ? IteratorClose(_iteratorRecord_, _v_).
            1. Let _status_ be Call(_adder_, _target_, « _k_.[[Value]], _v_.[[Value]] »).
            1. If _status_ is an abrupt completion, return ? IteratorClose(_iteratorRecord_, _status_).