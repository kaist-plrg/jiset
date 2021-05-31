        1. Let _len_ be ? ToIntegerOrInfinity(_argument_).
        1. If _len_ ≤ 0, return *+0*<sub>𝔽</sub>.
        1. Return 𝔽(min(_len_, 2<sup>53</sup> - 1)).