          1. If Type(_number_) is not Number, return *false*.
          1. If _number_ is *NaN*, *+∞*, or *-∞*, return *false*.
          1. Let _integer_ be ! ToInteger(_number_).
          1. If _integer_ is not equal to _number_, return *false*.
          1. If abs(_integer_) ≤ 2<sup>53</sup> - 1, return *true*.
          1. Otherwise, return *false*.