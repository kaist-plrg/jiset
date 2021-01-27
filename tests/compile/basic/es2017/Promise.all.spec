          1. Let _C_ be the *this* value.
          1. If Type(_C_) is not Object, throw a *TypeError* exception.
          1. Let _promiseCapability_ be ? NewPromiseCapability(_C_).
          1. Let _iterator_ be GetIterator(_iterable_).
          1. IfAbruptRejectPromise(_iterator_, _promiseCapability_).
          1. Let _iteratorRecord_ be Record { [[Iterator]]: _iterator_, [[Done]]: *false* }.
          1. Let _result_ be PerformPromiseAll(_iteratorRecord_, _C_, _promiseCapability_).
          1. If _result_ is an abrupt completion, then
            1. If _iteratorRecord_.[[Done]] is *false*, let _result_ be IteratorClose(_iterator_, _result_).
            1. IfAbruptRejectPromise(_result_, _promiseCapability_).
          1. Return Completion(_result_).