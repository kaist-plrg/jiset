          1. Let _Target_ be the *this* value.
          1. If IsCallable(_Target_) is *false*, throw a *TypeError* exception.
          1. Let _args_ be a new (possibly empty) List consisting of all of the argument values provided after _thisArg_ in order.
          1. Let _F_ be ? BoundFunctionCreate(_Target_, _thisArg_, _args_).
          1. Let _targetHasLength_ be ? HasOwnProperty(_Target_, `"length"`).
          1. If _targetHasLength_ is *true*, then
            1. Let _targetLen_ be ? Get(_Target_, `"length"`).
            1. If Type(_targetLen_) is not Number, let _L_ be 0.
            1. Else,
              1. Set _targetLen_ to ! ToInteger(_targetLen_).
              1. Let _L_ be the larger of 0 and the result of _targetLen_ minus the number of elements of _args_.
          1. Else, let _L_ be 0.
          1. Perform ! SetFunctionLength(_F_, _L_).
          1. Let _targetName_ be ? Get(_Target_, `"name"`).
          1. If Type(_targetName_) is not String, set _targetName_ to the empty string.
          1. Perform SetFunctionName(_F_, _targetName_, `"bound"`).
          1. Return _F_.