        1. Let _V_ be *undefined*.
        1. If the first |CaseClauses| is present, then
          1. Let _A_ be the List of |CaseClause| items in the first |CaseClauses|, in source text order.
        1. Else,
          1. Let _A_ be « ».
        1. Let _found_ be *false*.
        1. For each |CaseClause| _C_ in _A_, do
          1. If _found_ is *false*, then
            1. Set _found_ to ? CaseClauseIsSelected(_C_, _input_).
          1. If _found_ is *true*, then
            1. Let _R_ be the result of evaluating _C_.
            1. If _R_.[[Value]] is not ~empty~, set _V_ to _R_.[[Value]].
            1. If _R_ is an abrupt completion, return Completion(UpdateEmpty(_R_, _V_)).
        1. Let _foundInB_ be *false*.
        1. If the second |CaseClauses| is present, then
          1. Let _B_ be the List of |CaseClause| items in the second |CaseClauses|, in source text order.
        1. Else,
          1. Let _B_ be « ».
        1. If _found_ is *false*, then
          1. For each |CaseClause| _C_ in _B_, do
            1. If _foundInB_ is *false*, then
              1. Set _foundInB_ to ? CaseClauseIsSelected(_C_, _input_).
            1. If _foundInB_ is *true*, then
              1. Let _R_ be the result of evaluating |CaseClause| _C_.
              1. If _R_.[[Value]] is not ~empty~, set _V_ to _R_.[[Value]].
              1. If _R_ is an abrupt completion, return Completion(UpdateEmpty(_R_, _V_)).
        1. If _foundInB_ is *true*, return NormalCompletion(_V_).
        1. Let _R_ be the result of evaluating |DefaultClause|.
        1. If _R_.[[Value]] is not ~empty~, set _V_ to _R_.[[Value]].
        1. If _R_ is an abrupt completion, return Completion(UpdateEmpty(_R_, _V_)).
        1. For each |CaseClause| _C_ in _B_ (NOTE: this is another complete iteration of the second |CaseClauses|), do
          1. Let _R_ be the result of evaluating |CaseClause| _C_.
          1. If _R_.[[Value]] is not ~empty~, set _V_ to _R_.[[Value]].
          1. If _R_ is an abrupt completion, return Completion(UpdateEmpty(_R_, _V_)).
        1. Return NormalCompletion(_V_).