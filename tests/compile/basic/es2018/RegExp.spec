          1. Let _patternIsRegExp_ be ? IsRegExp(_pattern_).
          1. If NewTarget is *undefined*, then
            1. Let _newTarget_ be the active function object.
            1. If _patternIsRegExp_ is *true* and _flags_ is *undefined*, then
              1. Let _patternConstructor_ be ? Get(_pattern_, `"constructor"`).
              1. If SameValue(_newTarget_, _patternConstructor_) is *true*, return _pattern_.
          1. Else, let _newTarget_ be NewTarget.
          1. If Type(_pattern_) is Object and _pattern_ has a [[RegExpMatcher]] internal slot, then
            1. Let _P_ be _pattern_.[[OriginalSource]].
            1. If _flags_ is *undefined*, let _F_ be _pattern_.[[OriginalFlags]].
            1. Else, let _F_ be _flags_.
          1. Else if _patternIsRegExp_ is *true*, then
            1. Let _P_ be ? Get(_pattern_, `"source"`).
            1. If _flags_ is *undefined*, then
              1. Let _F_ be ? Get(_pattern_, `"flags"`).
            1. Else, let _F_ be _flags_.
          1. Else,
            1. Let _P_ be _pattern_.
            1. Let _F_ be _flags_.
          1. Let _O_ be ? RegExpAlloc(_newTarget_).
          1. Return ? RegExpInitialize(_O_, _P_, _F_).