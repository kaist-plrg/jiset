          1. Let _O_ be ? ToObject(*this* value).
          1. Let _len_ be ? ToLength(? Get(_O_, `"length"`)).
          1. Let _items_ be a List whose elements are, in left to right order, the arguments that were passed to this function invocation.
          1. Let _argCount_ be the number of elements in _items_.
          1. If _len_ + _argCount_ > 2<sup>53</sup>-1, throw a *TypeError* exception.
          1. Repeat, while _items_ is not empty
            1. Remove the first element from _items_ and let _E_ be the value of the element.
            1. Perform ? Set(_O_, ! ToString(_len_), _E_, *true*).
            1. Let _len_ be _len_+1.
          1. Perform ? Set(_O_, `"length"`, _len_, *true*).
          1. Return _len_.