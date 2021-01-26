          1. Let _args_ be the arguments object.
          1. Let _map_ be the value of the [[ParameterMap]] internal slot of _args_.
          1. Let _isMapped_ be ! HasOwnProperty(_map_, _P_).
          1. Let _result_ be ? OrdinaryDelete(_args_, _P_).
          1. If _result_ is *true* and the value of _isMapped_ is *true*, then
            1. Call _map_.[[Delete]](_P_).
          1. Return _result_.