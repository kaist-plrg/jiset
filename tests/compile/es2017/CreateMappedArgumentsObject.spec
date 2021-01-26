          1. Assert: _formals_ does not contain a rest parameter, any binding patterns, or any initializers. It may contain duplicate identifiers.
          1. Let _len_ be the number of elements in _argumentsList_.
          1. Let _obj_ be a newly created arguments exotic object with a [[ParameterMap]] internal slot.
          1. Set _obj_.[[GetOwnProperty]] as specified in <emu-xref href="#sec-arguments-exotic-objects-getownproperty-p"></emu-xref>.
          1. Set _obj_.[[DefineOwnProperty]] as specified in <emu-xref href="#sec-arguments-exotic-objects-defineownproperty-p-desc"></emu-xref>.
          1. Set _obj_.[[Get]] as specified in <emu-xref href="#sec-arguments-exotic-objects-get-p-receiver"></emu-xref>.
          1. Set _obj_.[[Set]] as specified in <emu-xref href="#sec-arguments-exotic-objects-set-p-v-receiver"></emu-xref>.
          1. Set _obj_.[[Delete]] as specified in <emu-xref href="#sec-arguments-exotic-objects-delete-p"></emu-xref>.
          1. Set the remainder of _obj_'s essential internal methods to the default ordinary object definitions specified in <emu-xref href="#sec-ordinary-object-internal-methods-and-internal-slots"></emu-xref>.
          1. Set _obj_.[[Prototype]] to %ObjectPrototype%.
          1. Set _obj_.[[Extensible]] to *true*.
          1. Let _map_ be ObjectCreate(*null*).
          1. Set _obj_.[[ParameterMap]] to _map_.
          1. Let _parameterNames_ be the BoundNames of _formals_.
          1. Let _numberOfParameters_ be the number of elements in _parameterNames_.
          1. Let _index_ be 0.
          1. Repeat, while _index_ < _len_,
            1. Let _val_ be _argumentsList_[_index_].
            1. Perform CreateDataProperty(_obj_, ! ToString(_index_), _val_).
            1. Let _index_ be _index_ + 1.
          1. Perform DefinePropertyOrThrow(_obj_, `"length"`, PropertyDescriptor{[[Value]]: _len_, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *true*}).
          1. Let _mappedNames_ be a new empty List.
          1. Let _index_ be _numberOfParameters_ - 1.
          1. Repeat, while _index_ ≥ 0,
            1. Let _name_ be _parameterNames_[_index_].
            1. If _name_ is not an element of _mappedNames_, then
              1. Add _name_ as an element of the list _mappedNames_.
              1. If _index_ < _len_, then
                1. Let _g_ be MakeArgGetter(_name_, _env_).
                1. Let _p_ be MakeArgSetter(_name_, _env_).
                1. Perform _map_.[[DefineOwnProperty]](! ToString(_index_), PropertyDescriptor{[[Set]]: _p_, [[Get]]: _g_, [[Enumerable]]: *false*, [[Configurable]]: *true*}).
            1. Let _index_ be _index_ - 1.
          1. Perform ! DefinePropertyOrThrow(_obj_, @@iterator, PropertyDescriptor {[[Value]]: %ArrayProto_values%, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *true*}).
          1. Perform ! DefinePropertyOrThrow(_obj_, `"callee"`, PropertyDescriptor {[[Value]]: _func_, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *true*}).
          1. Return _obj_.