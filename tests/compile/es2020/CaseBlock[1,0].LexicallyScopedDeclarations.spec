        1. If the first |CaseClauses| is present, let _declarations_ be the LexicallyScopedDeclarations of the first |CaseClauses|.
        1. Else, let _declarations_ be a new empty List.
        1. Append to _declarations_ the elements of the LexicallyScopedDeclarations of |DefaultClause|.
        1. If the second |CaseClauses| is not present, return _declarations_.
        1. Return the result of appending to _declarations_ the elements of the LexicallyScopedDeclarations of the second |CaseClauses|.