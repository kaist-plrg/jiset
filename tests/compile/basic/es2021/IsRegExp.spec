        1. If Type(_argument_) is not Object, return *false*.
        1. Let _matcher_ be ? Get(_argument_, @@match).
        1. If _matcher_ is not *undefined*, return ! ToBoolean(_matcher_).
        1. If _argument_ has a [[RegExpMatcher]] internal slot, return *true*.
        1. Return *false*.