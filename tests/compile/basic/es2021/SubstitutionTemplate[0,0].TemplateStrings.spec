          1. If _raw_ is *false*, then
            1. Let _head_ be the TV of |TemplateHead|.
          1. Else,
            1. Let _head_ be the TRV of |TemplateHead|.
          1. Let _tail_ be TemplateStrings of |TemplateSpans| with argument _raw_.
          1. Return a List whose elements are _head_ followed by the elements of _tail_.