        1. Let _len_ be ? ToInteger(_argument_).
        1. If _len_ ≤ *+0*, return *+0*.
        1. If _len_ is *+∞*, return 2<sup>53</sup>-1.
        1. Return min(_len_, 2<sup>53</sup>-1).