        1. Assert: Type(_target_) is Object.
        1. Assert: _excludedItems_ is a List of property keys.
        1. If _source_ is *undefined* or *null*, return _target_.
        1. Let _from_ be ! ToObject(_source_).
        1. Let _keys_ be ? _from_.[[OwnPropertyKeys]]().
        1. For each element _nextKey_ of _keys_ in List order, do
          1. Let _excluded_ be *false*.
          1. For each element _e_ of _excludedItems_ in List order, do
            1. If SameValue(_e_, _nextKey_) is *true*, then
              1. Set _excluded_ to *true*.
          1. If _excluded_ is *false*, then
            1. Let _desc_ be ? _from_.[[GetOwnProperty]](_nextKey_).
            1. If _desc_ is not *undefined* and _desc_.[[Enumerable]] is *true*, then
              1. Let _propValue_ be ? Get(_from_, _nextKey_).
              1. Perform ! CreateDataProperty(_target_, _nextKey_, _propValue_).
        1. Return _target_.