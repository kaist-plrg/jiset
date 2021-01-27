          1. Let _val_ be ? Get(_holder_, _name_).
          1. If Type(_val_) is Object, then
            1. Let _isArray_ be ? IsArray(_val_).
            1. If _isArray_ is *true*, then
              1. Let _I_ be 0.
              1. Let _len_ be ? ToLength(? Get(_val_, `"length"`)).
              1. Repeat, while _I_ < _len_,
                1. Let _newElement_ be ? InternalizeJSONProperty(_val_, ! ToString(_I_)).
                1. If _newElement_ is *undefined*, then
                  1. Perform ? _val_.[[Delete]](! ToString(_I_)).
                1. Else,
                  1. Perform ? CreateDataProperty(_val_, ! ToString(_I_), _newElement_).
                  1. NOTE: This algorithm intentionally does not throw an exception if CreateDataProperty returns *false*.
                1. Add 1 to _I_.
            1. Else,
              1. Let _keys_ be ? EnumerableOwnProperties(_val_, *"key"*).
              1. For each String _P_ in _keys_, do
                1. Let _newElement_ be ? InternalizeJSONProperty(_val_, _P_).
                1. If _newElement_ is *undefined*, then
                  1. Perform ? _val_.[[Delete]](_P_).
                1. Else,
                  1. Perform ? CreateDataProperty(_val_, _P_, _newElement_).
                  1. NOTE: This algorithm intentionally does not throw an exception if CreateDataProperty returns *false*.
          1. Return ? Call(_reviver_, _holder_, « _name_, _val_ »).