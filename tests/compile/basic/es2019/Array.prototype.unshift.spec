          1. Let _O_ be ? ToObject(*this* value).
          1. Let _len_ be ? ToLength(? Get(_O_, `"length"`)).
          1. Let _argCount_ be the number of actual arguments.
          1. If _argCount_ > 0, then
            1. If _len_ + _argCount_ > 2<sup>53</sup> - 1, throw a *TypeError* exception.
            1. Let _k_ be _len_.
            1. Repeat, while _k_ > 0,
              1. Let _from_ be ! ToString(_k_ - 1).
              1. Let _to_ be ! ToString(_k_ + _argCount_ - 1).
              1. Let _fromPresent_ be ? HasProperty(_O_, _from_).
              1. If _fromPresent_ is *true*, then
                1. Let _fromValue_ be ? Get(_O_, _from_).
                1. Perform ? Set(_O_, _to_, _fromValue_, *true*).
              1. Else _fromPresent_ is *false*,
                1. Perform ? DeletePropertyOrThrow(_O_, _to_).
              1. Decrease _k_ by 1.
            1. Let _j_ be 0.
            1. Let _items_ be a List whose elements are, in left to right order, the arguments that were passed to this function invocation.
            1. Repeat, while _items_ is not empty
              1. Remove the first element from _items_ and let _E_ be the value of that element.
              1. Perform ? Set(_O_, ! ToString(_j_), _E_, *true*).
              1. Increase _j_ by 1.
          1. Perform ? Set(_O_, `"length"`, _len_ + _argCount_, *true*).
          1. Return _len_ + _argCount_.