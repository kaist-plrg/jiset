          1. Let _Target_ be the *this* value.
          1. If IsCallable(_Target_) is *false*, throw a *TypeError* exception.
          1. Let _F_ be ? BoundFunctionCreate(_Target_, _thisArg_, _args_).
          1. Let _L_ be 0.
          1. Let _targetHasLength_ be ? HasOwnProperty(_Target_, *"length"*).
          1. If _targetHasLength_ is *true*, then
            1. Let _targetLen_ be ? Get(_Target_, *"length"*).
            1. If Type(_targetLen_) is Number, then
              1. If _targetLen_ is *+∞*<sub>𝔽</sub>, set _L_ to +∞.
              1. Else if _targetLen_ is *-∞*<sub>𝔽</sub>, set _L_ to 0.
              1. Else,
                1. Let _targetLenAsInt_ be ! ToIntegerOrInfinity(_targetLen_).
                1. Assert: _targetLenAsInt_ is finite.
                1. Let _argCount_ be the number of elements in _args_.
                1. Set _L_ to max(_targetLenAsInt_ - _argCount_, 0).
          1. Perform ! SetFunctionLength(_F_, _L_).
          1. Let _targetName_ be ? Get(_Target_, *"name"*).
          1. If Type(_targetName_) is not String, set _targetName_ to the empty String.
          1. Perform SetFunctionName(_F_, _targetName_, *"bound"*).
          1. Return _F_.