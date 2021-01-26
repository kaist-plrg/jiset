          1. If NewTarget is *undefined*, throw a *TypeError* exception.
          1. Let _set_ be ? OrdinaryCreateFromConstructor(NewTarget, `"%SetPrototype%"`, « [[SetData]] »).
          1. Set _set_.[[SetData]] to a new empty List.
          1. If _iterable_ is not present, let _iterable_ be *undefined*.
          1. If _iterable_ is either *undefined* or *null*, let _iter_ be *undefined*.
          1. Else,
            1. Let _adder_ be ? Get(_set_, `"add"`).
            1. If IsCallable(_adder_) is *false*, throw a *TypeError* exception.
            1. Let _iter_ be ? GetIterator(_iterable_).
          1. If _iter_ is *undefined*, return _set_.
          1. Repeat,
            1. Let _next_ be ? IteratorStep(_iter_).
            1. If _next_ is *false*, return _set_.
            1. Let _nextValue_ be ? IteratorValue(_next_).
            1. Let _status_ be Call(_adder_, _set_, « _nextValue_.[[Value]] »).
            1. If _status_ is an abrupt completion, return ? IteratorClose(_iter_, _status_).