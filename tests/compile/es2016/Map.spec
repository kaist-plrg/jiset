          1. If NewTarget is *undefined*, throw a *TypeError* exception.
          1. Let _map_ be ? OrdinaryCreateFromConstructor(NewTarget, `"%MapPrototype%"`, « [[MapData]] »).
          1. Set _map_'s [[MapData]] internal slot to a new empty List.
          1. If _iterable_ is not present, let _iterable_ be *undefined*.
          1. If _iterable_ is either *undefined* or *null*, let _iter_ be *undefined*.
          1. Else,
            1. Let _adder_ be ? Get(_map_, `"set"`).
            1. If IsCallable(_adder_) is *false*, throw a *TypeError* exception.
            1. Let _iter_ be ? GetIterator(_iterable_).
          1. If _iter_ is *undefined*, return _map_.
          1. Repeat
            1. Let _next_ be ? IteratorStep(_iter_).
            1. If _next_ is *false*, return _map_.
            1. Let _nextItem_ be ? IteratorValue(_next_).
            1. If Type(_nextItem_) is not Object, then
              1. Let _error_ be Completion{[[Type]]: ~throw~, [[Value]]: a newly created *TypeError* object, [[Target]]: ~empty~}.
              1. Return ? IteratorClose(_iter_, _error_).
            1. Let _k_ be Get(_nextItem_, `"0"`).
            1. If _k_ is an abrupt completion, return ? IteratorClose(_iter_, _k_).
            1. Let _v_ be Get(_nextItem_, `"1"`).
            1. If _v_ is an abrupt completion, return ? IteratorClose(_iter_, _v_).
            1. Let _status_ be Call(_adder_, _map_, « _k_.[[Value]], _v_.[[Value]] »).
            1. If _status_ is an abrupt completion, return ? IteratorClose(_iter_, _status_).