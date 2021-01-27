        1. If the first |CaseClauses| is present, then
          1. Let _hasDuplicates_ be ContainsDuplicateLabels of the first |CaseClauses| with argument _labelSet_.
          1. If _hasDuplicates_ is *true*, return *true*.
        1. Let _hasDuplicates_ be ContainsDuplicateLabels of |DefaultClause| with argument _labelSet_.
        1. If _hasDuplicates_ is *true*, return *true*.
        1. If the second |CaseClauses| is not present, return *false*.
        1. Return ContainsDuplicateLabels of the second |CaseClauses| with argument _labelSet_.