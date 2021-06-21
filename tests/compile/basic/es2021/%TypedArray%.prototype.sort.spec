          1. Assert: Both Type(_x_) and Type(_y_) are Number or both are BigInt.
          1. If _comparefn_ is not *undefined*, then
            1. Let _v_ be ? ToNumber(? Call(_comparefn_, *undefined*, « _x_, _y_ »)).
            1. If IsDetachedBuffer(_buffer_) is *true*, throw a *TypeError* exception.
            1. If _v_ is *NaN*, return *+0*<sub>𝔽</sub>.
            1. Return _v_.
          1. If _x_ and _y_ are both *NaN*, return *+0*<sub>𝔽</sub>.
          1. If _x_ is *NaN*, return *1*<sub>𝔽</sub>.
          1. If _y_ is *NaN*, return *-1*<sub>𝔽</sub>.
          1. If _x_ < _y_, return *-1*<sub>𝔽</sub>.
          1. If _x_ > _y_, return *1*<sub>𝔽</sub>.
          1. If _x_ is *-0*<sub>𝔽</sub> and _y_ is *+0*<sub>𝔽</sub>, return *-1*<sub>𝔽</sub>.
          1. If _x_ is *+0*<sub>𝔽</sub> and _y_ is *-0*<sub>𝔽</sub>, return *1*<sub>𝔽</sub>.
          1. Return *+0*<sub>𝔽</sub>.