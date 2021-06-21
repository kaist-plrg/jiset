          1. Assert: _literal_ is a |RegularExpressionLiteral|.
          1. If FlagText of _literal_ contains any code points other than `g`, `i`, `m`, `s`, `u`, or `y`, or if it contains the same code point more than once, return *false*.
          1. Let _patternText_ be BodyText of _literal_.
          1. If FlagText of _literal_ contains `u`, let _u_ be *true*; else let _u_ be *false*.
          1. If _u_ is *false*, then
            1. Let _stringValue_ be CodePointsToString(_patternText_).
            1. Set _patternText_ to the sequence of code points resulting from interpreting each of the 16-bit elements of _stringValue_ as a Unicode BMP code point. UTF-16 decoding is not applied to the elements.
          1. Let _parseResult_ be ParsePattern(_patternText_, _u_).
          1. If _parseResult_ is a Parse Node, return *true*; else return *false*.