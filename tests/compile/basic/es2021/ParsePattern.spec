            1. If _u_ is *true*, then
              1. Let _parseResult_ be ParseText(_patternText_, |Pattern[+U, +N]|).
            1. Else,
              1. Let _parseResult_ be ParseText(_patternText_, |Pattern[~U, ~N]|).
              1. If _parseResult_ is a Parse Node and _parseResult_ contains a |GroupName|, then
                1. Set _parseResult_ to ParseText(_patternText_, |Pattern[~U, +N]|).
            1. Return _parseResult_.