          1. Let _C_ be the *this* value.
          1. Let _promiseCapability_ be ? NewPromiseCapability(_C_).
          1. Let _promiseResolve_ be GetPromiseResolve(_C_).
          1. IfAbruptRejectPromise(_promiseResolve_, _promiseCapability_).
          1. Let _iteratorRecord_ be GetIterator(_iterable_).
          1. IfAbruptRejectPromise(_iteratorRecord_, _promiseCapability_).
          1. Let _result_ be PerformPromiseAllSettled(_iteratorRecord_, _C_, _promiseCapability_, _promiseResolve_).
          1. If _result_ is an abrupt completion, then
            1. If _iteratorRecord_.[[Done]] is *false*, set _result_ to IteratorClose(_iteratorRecord_, _result_).
            1. IfAbruptRejectPromise(_result_, _promiseCapability_).
          1. Return Completion(_result_).