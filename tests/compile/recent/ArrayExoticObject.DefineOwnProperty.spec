          1. Assert: IsPropertyKey(_P_) is *true*.
          1. If _P_ is *"length"*, then
            1. Return ? ArraySetLength(_A_, _Desc_).
          1. Else if _P_ is an array index, then
            1. Let _oldLenDesc_ be OrdinaryGetOwnProperty(_A_, *"length"*).
            1. Assert: ! IsDataDescriptor(_oldLenDesc_) is *true*.
            1. Assert: _oldLenDesc_.[[Configurable]] is *false*.
            1. Let _oldLen_ be _oldLenDesc_.[[Value]].
            1. Assert: _oldLen_ is a non-negative integral Number.
            1. Let _index_ be ! ToUint32(_P_).
            1. If _index_ ≥ _oldLen_ and _oldLenDesc_.[[Writable]] is *false*, return *false*.
            1. Let _succeeded_ be ! OrdinaryDefineOwnProperty(_A_, _P_, _Desc_).
            1. If _succeeded_ is *false*, return *false*.
            1. If _index_ ≥ _oldLen_, then
              1. Set _oldLenDesc_.[[Value]] to _index_ + *1*<sub>𝔽</sub>.
              1. Let _succeeded_ be OrdinaryDefineOwnProperty(_A_, *"length"*, _oldLenDesc_).
              1. Assert: _succeeded_ is *true*.
            1. Return *true*.
          1. Return OrdinaryDefineOwnProperty(_A_, _P_, _Desc_).