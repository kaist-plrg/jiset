          1. Assert: IsPropertyKey(_P_) is *true*.
          1. Let _stringDesc_ be ! StringGetOwnProperty(_S_, _P_).
          1. If _stringDesc_ is not *undefined*, then
            1. Let _extensible_ be _S_.[[Extensible]].
            1. Return ! IsCompatiblePropertyDescriptor(_extensible_, _Desc_, _stringDesc_).
          1. Return ! OrdinaryDefineOwnProperty(_S_, _P_, _Desc_).