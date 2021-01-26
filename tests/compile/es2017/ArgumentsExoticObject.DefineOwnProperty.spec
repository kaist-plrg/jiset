          1. Let _args_ be the arguments object.
          1. Let _map_ be _args_.[[ParameterMap]].
          1. Let _isMapped_ be HasOwnProperty(_map_, _P_).
          1. Let _newArgDesc_ be _Desc_.
          1. If _isMapped_ is *true* and IsDataDescriptor(_Desc_) is *true*, then
            1. If _Desc_.[[Value]] is not present and _Desc_.[[Writable]] is present and its value is *false*, then
              1. Set _newArgDesc_ to a copy of _Desc_.
              1. Set _newArgDesc_.[[Value]] to Get(_map_, _P_).
          1. Let _allowed_ be ? OrdinaryDefineOwnProperty(_args_, _P_, _newArgDesc_).
          1. If _allowed_ is *false*, return *false*.
          1. If _isMapped_ is *true*, then
            1. If IsAccessorDescriptor(_Desc_) is *true*, then
              1. Call _map_.[[Delete]](_P_).
            1. Else,
              1. If _Desc_.[[Value]] is present, then
                1. Let _setStatus_ be Set(_map_, _P_, _Desc_.[[Value]], *false*).
                1. Assert: _setStatus_ is *true* because formal parameters mapped by argument objects are always writable.
              1. If _Desc_.[[Writable]] is present and its value is *false*, then
                1. Call _map_.[[Delete]](_P_).
          1. Return *true*.