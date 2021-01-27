          1. Let _R_ be the *this* value.
          1. If Type(_R_) is not Object, throw a *TypeError* exception.
          1. Let _result_ be the empty String.
          1. Let _global_ be ToBoolean(? Get(_R_, `"global"`)).
          1. If _global_ is *true*, append `"g"` as the last code unit of _result_.
          1. Let _ignoreCase_ be ToBoolean(? Get(_R_, `"ignoreCase"`)).
          1. If _ignoreCase_ is *true*, append `"i"` as the last code unit of _result_.
          1. Let _multiline_ be ToBoolean(? Get(_R_, `"multiline"`)).
          1. If _multiline_ is *true*, append `"m"` as the last code unit of _result_.
          1. Let _unicode_ be ToBoolean(? Get(_R_, `"unicode"`)).
          1. If _unicode_ is *true*, append `"u"` as the last code unit of _result_.
          1. Let _sticky_ be ToBoolean(? Get(_R_, `"sticky"`)).
          1. If _sticky_ is *true*, append `"y"` as the last code unit of _result_.
          1. Return _result_.