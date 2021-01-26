          1. Let _args_ be the arguments object.
          1. If _P_ is `"caller"`, then
            1. Let _desc_ be ! OrdinaryGetOwnProperty(_args_, _P_).
            1. If IsDataDescriptor(_desc_) is *true*, return *true*.
          1. Return ? OrdinaryHasProperty(_args_, _P_).