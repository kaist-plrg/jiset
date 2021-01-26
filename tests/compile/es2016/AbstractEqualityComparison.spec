        1. If Type(_x_) is the same as Type(_y_), then
          1. Return the result of performing Strict Equality Comparison _x_ === _y_.
        1. If _x_ is *null* and _y_ is *undefined*, return *true*.
        1. If _x_ is *undefined* and _y_ is *null*, return *true*.
        1. If Type(_x_) is Number and Type(_y_) is String, return the result of the comparison _x_ == ToNumber(_y_).
        1. If Type(_x_) is String and Type(_y_) is Number, return the result of the comparison ToNumber(_x_) == _y_.
        1. If Type(_x_) is Boolean, return the result of the comparison ToNumber(_x_) == _y_.
        1. If Type(_y_) is Boolean, return the result of the comparison _x_ == ToNumber(_y_).
        1. If Type(_x_) is either String, Number, or Symbol and Type(_y_) is Object, return the result of the comparison _x_ == ToPrimitive(_y_).
        1. If Type(_x_) is Object and Type(_y_) is either String, Number, or Symbol, return the result of the comparison ToPrimitive(_x_) == _y_.
        1. Return *false*.