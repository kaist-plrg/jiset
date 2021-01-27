            1. Let _obj_ be ? ToObject(_O_).
            1. Let _keys_ be ? _obj_.[[OwnPropertyKeys]]().
            1. Let _nameList_ be a new empty List.
            1. For each element _nextKey_ of _keys_ in List order, do
              1. If Type(_nextKey_) is Symbol and _type_ is ~symbol~ or Type(_nextKey_) is String and _type_ is ~string~, then
                1. Append _nextKey_ as the last element of _nameList_.
            1. Return CreateArrayFromList(_nameList_).