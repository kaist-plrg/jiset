          1. Let _args_ be the arguments object.
          1. Let _desc_ be OrdinaryGetOwnProperty(_args_, _P_).
          1. If _desc_ is *undefined*, return _desc_.
          1. Let _map_ be _args_.[[ParameterMap]].
          1. Let _isMapped_ be ! HasOwnProperty(_map_, _P_).
          1. If _isMapped_ is *true*, then
            1. Set _desc_.[[Value]] to Get(_map_, _P_).
          1. Return _desc_.