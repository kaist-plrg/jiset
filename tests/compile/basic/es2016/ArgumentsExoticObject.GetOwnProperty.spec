          1. Let _args_ be the arguments object.
          1. Let _desc_ be OrdinaryGetOwnProperty(_args_, _P_).
          1. If _desc_ is *undefined*, return _desc_.
          1. Let _map_ be the value of the [[ParameterMap]] internal slot of the arguments object.
          1. Let _isMapped_ be ! HasOwnProperty(_map_, _P_).
          1. If the value of _isMapped_ is *true*, then
            1. Set _desc_.[[Value]] to Get(_map_, _P_).
          1. If IsDataDescriptor(_desc_) is *true* and _P_ is `"caller"` and _desc_.[[Value]] is a strict mode Function object, throw a *TypeError* exception.
          1. Return _desc_.