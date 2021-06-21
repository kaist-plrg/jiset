        1. If the first |CaseClauses| is present, let _names_ be the VarDeclaredNames of the first |CaseClauses|.
        1. Else, let _names_ be a new empty List.
        1. Append to _names_ the elements of the VarDeclaredNames of |DefaultClause|.
        1. If the second |CaseClauses| is not present, return _names_.
        1. Return the result of appending to _names_ the elements of the VarDeclaredNames of the second |CaseClauses|.