        1. Let _hasUndefinedLabels_ be ContainsUndefinedBreakTarget of |Block| with argument _labelSet_.
        1. If _hasUndefinedLabels_ is *true*, return *true*.
        1. Let _hasUndefinedLabels_ be ContainsUndefinedBreakTarget of |Catch| with argument _labelSet_.
        1. If _hasUndefinedLabels_ is *true*, return *true*.
        1. Return ContainsUndefinedBreakTarget of |Finally| with argument _labelSet_.