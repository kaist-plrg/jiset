          1. If SameValue(_args_, _Receiver_) is *false*, then
            1. Let _isMapped_ be *false*.
          1. Else,
            1. Let _map_ be _args_.[[ParameterMap]].
            1. Let _isMapped_ be ! HasOwnProperty(_map_, _P_).
          1. If _isMapped_ is *true*, then
            1. Let _setStatus_ be Set(_map_, _P_, _V_, *false*).
            1. Assert: _setStatus_ is *true* because formal parameters mapped by argument objects are always writable.
          1. Return ? OrdinarySet(_args_, _P_, _V_, _Receiver_).