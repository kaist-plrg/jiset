            1. If _pattern_ is *undefined*, let _P_ be the empty String.
            1. Else, let _P_ be ? ToString(_pattern_).
            1. If _flags_ is *undefined*, let _F_ be the empty String.
            1. Else, let _F_ be ? ToString(_flags_).
            1. If _F_ contains any code unit other than *"g"*, *"i"*, *"m"*, *"s"*, *"u"*, or *"y"* or if it contains the same code unit more than once, throw a *SyntaxError* exception.
            1. If _F_ contains *"u"*, let _BMP_ be *false*; else let _BMP_ be *true*.
            1. If _BMP_ is *true*, then
              1. Let _pText_ be the sequence of code points resulting from interpreting each of the 16-bit elements of _P_ as a Unicode BMP code point. UTF-16 decoding is not applied to the elements.
              1. Parse _pText_ using the grammars in <emu-xref href="#sec-patterns"></emu-xref>. The goal symbol for the parse is |Pattern[~U, ~N]|. If the result of parsing contains a |GroupName|, reparse with the goal symbol |Pattern[~U, +N]| and use this result instead. Throw a *SyntaxError* exception if _pText_ did not conform to the grammar, if any elements of _pText_ were not matched by the parse, or if any Early Error conditions exist.
              1. Let _patternCharacters_ be a List whose elements are the code unit elements of _P_.
            1. Else,
              1. Let _pText_ be ! UTF16DecodeString(_P_).
              1. Parse _pText_ using the grammars in <emu-xref href="#sec-patterns"></emu-xref>. The goal symbol for the parse is |Pattern[+U, +N]|. Throw a *SyntaxError* exception if _pText_ did not conform to the grammar, if any elements of _pText_ were not matched by the parse, or if any Early Error conditions exist.
              1. Let _patternCharacters_ be a List whose elements are the code points of _pText_.
            1. Set _obj_.[[OriginalSource]] to _P_.
            1. Set _obj_.[[OriginalFlags]] to _F_.
            1. Set _obj_.[[RegExpMatcher]] to the abstract closure that evaluates the above parse by applying the semantics provided in <emu-xref href="#sec-pattern-semantics"></emu-xref> using _patternCharacters_ as the pattern's List of |SourceCharacter| values and _F_ as the flag parameters.
            1. Perform ? Set(_obj_, *"lastIndex"*, 0, *true*).
            1. Return _obj_.