          1. Let _to_ be ? ToObject(_target_).
          1. If only one argument was passed, return _to_.
          1. Let _sources_ be the List of argument values starting with the second argument.
          1. For each element _nextSource_ of _sources_, in ascending index order, do
            1. If _nextSource_ is *undefined* or *null*, let _keys_ be a new empty List.
            1. Else,
              1. Let _from_ be ! ToObject(_nextSource_).
              1. Let _keys_ be ? _from_.[[OwnPropertyKeys]]().
            1. For each element _nextKey_ of _keys_ in List order, do
              1. Let _desc_ be ? _from_.[[GetOwnProperty]](_nextKey_).
              1. If _desc_ is not *undefined* and _desc_.[[Enumerable]] is *true*, then
                1. Let _propValue_ be ? Get(_from_, _nextKey_).
                1. Perform ? Set(_to_, _nextKey_, _propValue_, *true*).
          1. Return _to_.