          1. Let _map_ be _args_.[[ParameterMap]].
          1. Let _isMapped_ be ! HasOwnProperty(_map_, _P_).
          1. Let _result_ be ? OrdinaryDelete(_args_, _P_).
          1. If _result_ is *true* and _isMapped_ is *true*, then
            1. Call _map_.[[Delete]](_P_).
          1. Return _result_.