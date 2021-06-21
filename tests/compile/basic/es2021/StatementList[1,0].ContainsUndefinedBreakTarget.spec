        1. Let _hasUndefinedLabels_ be ContainsUndefinedBreakTarget of |StatementList| with argument _labelSet_.
        1. If _hasUndefinedLabels_ is *true*, return *true*.
        1. Return ContainsUndefinedBreakTarget of |StatementListItem| with argument _labelSet_.