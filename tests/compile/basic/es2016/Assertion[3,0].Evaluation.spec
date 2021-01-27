          1. Let _e_ be _x_'s _endIndex_.
          1. Call IsWordChar(_e_-1) and let _a_ be the Boolean result.
          1. Call IsWordChar(_e_) and let _b_ be the Boolean result.
          1. If _a_ is *true* and _b_ is *false*, return *false*.
          1. If _a_ is *false* and _b_ is *true*, return *false*.
          1. Return *true*.