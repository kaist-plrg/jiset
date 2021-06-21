          1. Let _len_ be the number of elements in _argumentsList_.
          1. Let _obj_ be ! OrdinaryObjectCreate(%Object.prototype%, « [[ParameterMap]] »).
          1. Set _obj_.[[ParameterMap]] to *undefined*.
          1. Perform DefinePropertyOrThrow(_obj_, *"length"*, PropertyDescriptor { [[Value]]: 𝔽(_len_), [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *true* }).
          1. Let _index_ be 0.
          1. Repeat, while _index_ < _len_,
            1. Let _val_ be _argumentsList_[_index_].
            1. Perform ! CreateDataPropertyOrThrow(_obj_, ! ToString(𝔽(_index_)), _val_).
            1. Set _index_ to _index_ + 1.
          1. Perform ! DefinePropertyOrThrow(_obj_, @@iterator, PropertyDescriptor { [[Value]]: %Array.prototype.values%, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *true* }).
          1. Perform ! DefinePropertyOrThrow(_obj_, *"callee"*, PropertyDescriptor { [[Get]]: %ThrowTypeError%, [[Set]]: %ThrowTypeError%, [[Enumerable]]: *false*, [[Configurable]]: *false* }).
          1. Return _obj_.