        1. Let _hasUndefinedLabels_ be ContainsUndefinedBreakTarget of the first |Statement| with argument _labelSet_.
        1. If _hasUndefinedLabels_ is *true*, return *true*.
        1. Return ContainsUndefinedBreakTarget of the second |Statement| with argument _labelSet_.