        1. If Type(_argument_) is not Number, return *false*.
        1. If _argument_ is *NaN*, *+∞*<sub>𝔽</sub>, or *-∞*<sub>𝔽</sub>, return *false*.
        1. If floor(abs(ℝ(_argument_))) ≠ abs(ℝ(_argument_)), return *false*.
        1. Return *true*.