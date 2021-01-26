            1. If Type(_O_) is not Object, throw a *TypeError* exception.
            1. Let _props_ be ? ToObject(_Properties_).
            1. Let _keys_ be ? _props_.[[OwnPropertyKeys]]().
            1. Let _descriptors_ be a new empty List.
            1. Repeat for each element _nextKey_ of _keys_ in List order,
              1. Let _propDesc_ be ? _props_.[[GetOwnProperty]](_nextKey_).
              1. If _propDesc_ is not *undefined* and _propDesc_.[[Enumerable]] is *true*, then
                1. Let _descObj_ be ? Get(_props_, _nextKey_).
                1. Let _desc_ be ? ToPropertyDescriptor(_descObj_).
                1. Append the pair (a two element List) consisting of _nextKey_ and _desc_ to the end of _descriptors_.
            1. For each _pair_ from _descriptors_ in list order,
              1. Let _P_ be the first element of _pair_.
              1. Let _desc_ be the second element of _pair_.
              1. Perform ? DefinePropertyOrThrow(_O_, _P_, _desc_).
            1. Return _O_.