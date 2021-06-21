          1. Let _front_ be TemplateStrings of |TemplateMiddleList| with argument _raw_.
          1. If _raw_ is *false*, then
            1. Let _last_ be the TV of |TemplateMiddle|.
          1. Else,
            1. Let _last_ be the TRV of |TemplateMiddle|.
          1. Append _last_ as the last element of the List _front_.
          1. Return _front_.