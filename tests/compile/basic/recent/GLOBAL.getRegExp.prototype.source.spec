          1. Let _R_ be the *this* value.
          1. If Type(_R_) is not Object, throw a *TypeError* exception.
          1. If _R_ does not have an [[OriginalSource]] internal slot, then
            1. If SameValue(_R_, %RegExp.prototype%) is *true*, return *"(?:)"*.
            1. Otherwise, throw a *TypeError* exception.
          1. Assert: _R_ has an [[OriginalFlags]] internal slot.
          1. Let _src_ be _R_.[[OriginalSource]].
          1. Let _flags_ be _R_.[[OriginalFlags]].
          1. Return EscapeRegExpPattern(_src_, _flags_).