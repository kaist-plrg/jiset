          1. Let _has_ be *false*.
          1. If the first |CaseClauses| is present, let _has_ be HasCallInTailPosition of the first |CaseClauses| with argument _call_.
          1. If _has_ is *true*, return *true*.
          1. Let _has_ be HasCallInTailPosition of the |DefaultClause| with argument _call_.
          1. If _has_ is *true*, return *true*.
          1. If the second |CaseClauses| is present, let _has_ be HasCallInTailPosition of the second |CaseClauses| with argument _call_.
          1. Return _has_.