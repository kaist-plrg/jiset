          1. Let _len_ be the number of elements in _argumentsList_.
          1. Let _obj_ be ObjectCreate(%ObjectPrototype%, « [[ParameterMap]] »).
          1. Set _obj_'s [[ParameterMap]] internal slot to *undefined*.
          1. Perform DefinePropertyOrThrow(_obj_, `"length"`, PropertyDescriptor{[[Value]]: _len_, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *true*}).
          1. Let _index_ be 0.
          1. Repeat while _index_ < _len_,
            1. Let _val_ be _argumentsList_[_index_].
            1. Perform CreateDataProperty(_obj_, ! ToString(_index_), _val_).
            1. Let _index_ be _index_ + 1.
          1. Perform ! DefinePropertyOrThrow(_obj_, @@iterator, PropertyDescriptor {[[Value]]: %ArrayProto_values%, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *true*}).
          1. Perform ! DefinePropertyOrThrow(_obj_, `"callee"`, PropertyDescriptor {[[Get]]: %ThrowTypeError%, [[Set]]: %ThrowTypeError%, [[Enumerable]]: *false*, [[Configurable]]: *false*}).
          1. Perform ! DefinePropertyOrThrow(_obj_, `"caller"`, PropertyDescriptor {[[Get]]: %ThrowTypeError%, [[Set]]: %ThrowTypeError%, [[Enumerable]]: *false*, [[Configurable]]: *false*}).
          1. Return _obj_.