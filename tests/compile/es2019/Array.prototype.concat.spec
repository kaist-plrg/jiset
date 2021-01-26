          1. Let _O_ be ? ToObject(*this* value).
          1. Let _A_ be ? ArraySpeciesCreate(_O_, 0).
          1. Let _n_ be 0.
          1. Let _items_ be a List whose first element is _O_ and whose subsequent elements are, in left to right order, the arguments that were passed to this function invocation.
          1. Repeat, while _items_ is not empty
            1. Remove the first element from _items_ and let _E_ be the value of the element.
            1. Let _spreadable_ be ? IsConcatSpreadable(_E_).
            1. If _spreadable_ is *true*, then
              1. Let _k_ be 0.
              1. Let _len_ be ? ToLength(? Get(_E_, `"length"`)).
              1. If _n_ + _len_ > 2<sup>53</sup> - 1, throw a *TypeError* exception.
              1. Repeat, while _k_ < _len_
                1. Let _P_ be ! ToString(_k_).
                1. Let _exists_ be ? HasProperty(_E_, _P_).
                1. If _exists_ is *true*, then
                  1. Let _subElement_ be ? Get(_E_, _P_).
                  1. Perform ? CreateDataPropertyOrThrow(_A_, ! ToString(_n_), _subElement_).
                1. Increase _n_ by 1.
                1. Increase _k_ by 1.
            1. Else _E_ is added as a single item rather than spread,
              1. If _n_ ≥ 2<sup>53</sup> - 1, throw a *TypeError* exception.
              1. Perform ? CreateDataPropertyOrThrow(_A_, ! ToString(_n_), _E_).
              1. Increase _n_ by 1.
          1. Perform ? Set(_A_, `"length"`, _n_, *true*).
          1. Return _A_.