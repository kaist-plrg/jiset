            1. If _x_ is *NaN*, return *undefined*.
            1. If _y_ is *NaN*, return *undefined*.
            1. If _x_ and _y_ are the same Number value, return *false*.
            1. If _x_ is *+0* and _y_ is *-0*, return *false*.
            1. If _x_ is *-0* and _y_ is *+0*, return *false*.
            1. If _x_ is *+∞*, return *false*.
            1. If _y_ is *+∞*, return *true*.
            1. If _y_ is *-∞*, return *false*.
            1. If _x_ is *-∞*, return *true*.
            1. If the mathematical value of _x_ is less than the mathematical value of _y_—note that these mathematical values are both finite and not both zero—return *true*. Otherwise, return *false*.