        1. Let _hasUndefinedLabels_ be ContainsUndefinedContinueTarget of |Block| with arguments _iterationSet_ and « ».
        1. If _hasUndefinedLabels_ is *true*, return *true*.
        1. Return ContainsUndefinedContinueTarget of |Finally| with arguments _iterationSet_ and « ».