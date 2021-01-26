          1. Let _args_ be the arguments object.
          1. Let _map_ be _args_.[[ParameterMap]].
          1. Let _isMapped_ be ! HasOwnProperty(_map_, _P_).
          1. If _isMapped_ is *false*, then
            1. Return ? OrdinaryGet(_args_, _P_, _Receiver_).
          1. Else,
            1. Assert: _map_ contains a formal parameter mapping for _P_.
            1. Return Get(_map_, _P_).