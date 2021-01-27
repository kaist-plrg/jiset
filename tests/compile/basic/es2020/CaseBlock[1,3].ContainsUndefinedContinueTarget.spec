        1. If the first |CaseClauses| is present, then
          1. Let _hasUndefinedLabels_ be ContainsUndefinedContinueTarget of the first |CaseClauses| with arguments _iterationSet_ and « ».
          1. If _hasUndefinedLabels_ is *true*, return *true*.
        1. Let _hasUndefinedLabels_ be ContainsUndefinedContinueTarget of |DefaultClause| with arguments _iterationSet_ and « ».
        1. If _hasUndefinedLabels_ is *true*, return *true*.
        1. If the second |CaseClauses| is not present, return *false*.
        1. Return ContainsUndefinedContinueTarget of the second |CaseClauses| with arguments _iterationSet_ and « ».