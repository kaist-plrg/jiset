          1. Let _e_ be _x_'s _endIndex_.
          1. If _e_ is equal to _InputLength_, return *true*.
          1. If _Multiline_ is *false*, return *false*.
          1. If the character _Input_[_e_] is one of |LineTerminator|, return *true*.
          1. Return *false*.