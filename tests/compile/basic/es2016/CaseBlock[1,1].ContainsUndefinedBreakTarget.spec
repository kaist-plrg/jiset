        1. If the first |CaseClauses| is present, then
          1. Let _hasUndefinedLabels_ be ContainsUndefinedBreakTarget of the first |CaseClauses| with argument _labelSet_.
          1. If _hasUndefinedLabels_ is *true*, return *true*.
        1. Let _hasUndefinedLabels_ be ContainsUndefinedBreakTarget of |DefaultClause| with argument _labelSet_.
        1. If _hasUndefinedLabels_ is *true*, return *true*.
        1. If the second |CaseClauses| is not present, return *false*.
        1. Return ContainsUndefinedBreakTarget of the second |CaseClauses| with argument _labelSet_.