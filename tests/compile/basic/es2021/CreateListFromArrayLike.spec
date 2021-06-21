        1. If _elementTypes_ is not present, set _elementTypes_ to « Undefined, Null, Boolean, String, Symbol, Number, BigInt, Object ».
        1. If Type(_obj_) is not Object, throw a *TypeError* exception.
        1. Let _len_ be ? LengthOfArrayLike(_obj_).
        1. Let _list_ be a new empty List.
        1. Let _index_ be 0.
        1. Repeat, while _index_ < _len_,
          1. Let _indexName_ be ! ToString(𝔽(_index_)).
          1. Let _next_ be ? Get(_obj_, _indexName_).
          1. If Type(_next_) is not an element of _elementTypes_, throw a *TypeError* exception.
          1. Append _next_ as the last element of _list_.
          1. Set _index_ to _index_ + 1.
        1. Return _list_.