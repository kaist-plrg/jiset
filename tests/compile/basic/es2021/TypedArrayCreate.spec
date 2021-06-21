          1. Let _newTypedArray_ be ? Construct(_constructor_, _argumentList_).
          1. Perform ? ValidateTypedArray(_newTypedArray_).
          1. If _argumentList_ is a List of a single Number, then
            1. If _newTypedArray_.[[ArrayLength]] < ℝ(_argumentList_[0]), throw a *TypeError* exception.
          1. Return _newTypedArray_.