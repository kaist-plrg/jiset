          1. Let _middle_ be TemplateStrings of |TemplateMiddleList| with argument _raw_.
          1. If _raw_ is *false*, then
            1. Let _tail_ be the TV of |TemplateTail|.
          1. Else,
            1. Let _tail_ be the TRV of |TemplateTail|.
          1. Return a List whose elements are the elements of _middle_ followed by _tail_.