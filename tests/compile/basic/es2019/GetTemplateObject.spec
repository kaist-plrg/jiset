          1. Let _rawStrings_ be TemplateStrings of _templateLiteral_ with argument *true*.
          1. Let _realm_ be the current Realm Record.
          1. Let _templateRegistry_ be _realm_.[[TemplateMap]].
          1. For each element _e_ of _templateRegistry_, do
            1. If _e_.[[Site]] is the same Parse Node as _templateLiteral_, then
              1. Return _e_.[[Array]].
          1. Let _cookedStrings_ be TemplateStrings of _templateLiteral_ with argument *false*.
          1. Let _count_ be the number of elements in the List _cookedStrings_.
          1. Assert: _count_ ≤ 2<sup>32</sup> - 1.
          1. Let _template_ be ! ArrayCreate(_count_).
          1. Let _rawObj_ be ! ArrayCreate(_count_).
          1. Let _index_ be 0.
          1. Repeat, while _index_ < _count_
            1. Let _prop_ be ! ToString(_index_).
            1. Let _cookedValue_ be the String value _cookedStrings_[_index_].
            1. Call _template_.[[DefineOwnProperty]](_prop_, PropertyDescriptor { [[Value]]: _cookedValue_, [[Writable]]: *false*, [[Enumerable]]: *true*, [[Configurable]]: *false* }).
            1. Let _rawValue_ be the String value _rawStrings_[_index_].
            1. Call _rawObj_.[[DefineOwnProperty]](_prop_, PropertyDescriptor { [[Value]]: _rawValue_, [[Writable]]: *false*, [[Enumerable]]: *true*, [[Configurable]]: *false* }).
            1. Increase _index_ by 1.
          1. Perform SetIntegrityLevel(_rawObj_, `"frozen"`).
          1. Call _template_.[[DefineOwnProperty]](`"raw"`, PropertyDescriptor { [[Value]]: _rawObj_, [[Writable]]: *false*, [[Enumerable]]: *false*, [[Configurable]]: *false* }).
          1. Perform SetIntegrityLevel(_template_, `"frozen"`).
          1. Append the Record { [[Site]]: _templateLiteral_, [[Array]]: _template_ } to _templateRegistry_.
          1. Return _template_.