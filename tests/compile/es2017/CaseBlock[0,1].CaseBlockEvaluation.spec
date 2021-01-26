        1. Let _V_ be *undefined*.
        1. Let _A_ be the List of |CaseClause| items in |CaseClauses|, in source text order.
        1. Let _found_ be *false*.
        1. For each |CaseClause| _C_ in _A_, do
          1. If _found_ is *false*, then
            1. Let _clauseSelector_ be the result of CaseSelectorEvaluation of _C_.
            1. ReturnIfAbrupt(_clauseSelector_).
            1. Set _found_ to the result of performing Strict Equality Comparison _input_ === _clauseSelector_.
          1. If _found_ is *true*, then
            1. Let _R_ be the result of evaluating _C_.
            1. If _R_.[[Value]] is not ~empty~, set _V_ to _R_.[[Value]].
            1. If _R_ is an abrupt completion, return Completion(UpdateEmpty(_R_, _V_)).
        1. Return NormalCompletion(_V_).