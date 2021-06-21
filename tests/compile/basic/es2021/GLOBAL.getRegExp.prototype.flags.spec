          1. Let _R_ be the *this* value.
          1. If Type(_R_) is not Object, throw a *TypeError* exception.
          1. Let _result_ be the empty String.
          1. Let _global_ be ! ToBoolean(? Get(_R_, *"global"*)).
          1. If _global_ is *true*, append the code unit 0x0067 (LATIN SMALL LETTER G) as the last code unit of _result_.
          1. Let _ignoreCase_ be ! ToBoolean(? Get(_R_, *"ignoreCase"*)).
          1. If _ignoreCase_ is *true*, append the code unit 0x0069 (LATIN SMALL LETTER I) as the last code unit of _result_.
          1. Let _multiline_ be ! ToBoolean(? Get(_R_, *"multiline"*)).
          1. If _multiline_ is *true*, append the code unit 0x006D (LATIN SMALL LETTER M) as the last code unit of _result_.
          1. Let _dotAll_ be ! ToBoolean(? Get(_R_, *"dotAll"*)).
          1. If _dotAll_ is *true*, append the code unit 0x0073 (LATIN SMALL LETTER S) as the last code unit of _result_.
          1. Let _unicode_ be ! ToBoolean(? Get(_R_, *"unicode"*)).
          1. If _unicode_ is *true*, append the code unit 0x0075 (LATIN SMALL LETTER U) as the last code unit of _result_.
          1. Let _sticky_ be ! ToBoolean(? Get(_R_, *"sticky"*)).
          1. If _sticky_ is *true*, append the code unit 0x0079 (LATIN SMALL LETTER Y) as the last code unit of _result_.
          1. Return _result_.