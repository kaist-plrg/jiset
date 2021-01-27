          1. Assert: _literal_ is a |RegularExpressionLiteral|.
          1. If FlagText of _literal_ contains any code points other than `g`, `i`, `m`, `s`, `u`, or `y`, or if it contains the same code point more than once, return *false*.
          1. Let _P_ be BodyText of _literal_.
          1. If FlagText of _literal_ contains `u`, then
            1. Parse _P_ using the grammars in <emu-xref href="#sec-patterns"></emu-xref>. The goal symbol for the parse is |Pattern[+U, +N]|. If _P_ did not conform to the grammar, if any elements of _P_ were not matched by the parse, or if any Early Error conditions exist, return *false*. Otherwise, return *true*.
          1. Let _stringValue_ be UTF16Encode(_P_).
          1. Let _pText_ be the sequence of code points resulting from interpreting each of the 16-bit elements of _stringValue_ as a Unicode BMP code point. UTF-16 decoding is not applied to the elements.
          1. Parse _pText_ using the grammars in <emu-xref href="#sec-patterns"></emu-xref>. The goal symbol for the parse is |Pattern[~U, ~N]|. If the result of parsing contains a |GroupName|, reparse with the goal symbol |Pattern[~U, +N]|. If _P_ did not conform to the grammar, if any elements of _P_ were not matched by the parse, or if any Early Error conditions exist, return *false*. Otherwise, return *true*.