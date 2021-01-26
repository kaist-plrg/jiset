        1. If the first |CaseClauses| is present, let _declarations_ be the VarScopedDeclarations of the first |CaseClauses|.
        1. Else let _declarations_ be a new empty List.
        1. Append to _declarations_ the elements of the VarScopedDeclarations of the |DefaultClause|.
        1. If the second |CaseClauses| is not present, return _declarations_.
        1. Else return the result of appending to _declarations_ the elements of the VarScopedDeclarations of the second |CaseClauses|.