          1. Let _rx_ be the *this* value.
          1. If Type(_rx_) is not Object, throw a *TypeError* exception.
          1. Let _S_ be ? ToString(_string_).
          1. Let _previousLastIndex_ be ? Get(_rx_, `"lastIndex"`).
          1. Perform ? Set(_rx_, `"lastIndex"`, 0, *true*).
          1. Let _result_ be ? RegExpExec(_rx_, _S_).
          1. Perform ? Set(_rx_, `"lastIndex"`, _previousLastIndex_, *true*).
          1. If _result_ is *null*, return -1.
          1. Return ? Get(_result_, `"index"`).