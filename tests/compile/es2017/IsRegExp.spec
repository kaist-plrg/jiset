        1. If Type(_argument_) is not Object, return *false*.
        1. Let _isRegExp_ be ? Get(_argument_, @@match).
        1. If _isRegExp_ is not *undefined*, return ToBoolean(_isRegExp_).
        1. If _argument_ has a [[RegExpMatcher]] internal slot, return *true*.
        1. Return *false*.