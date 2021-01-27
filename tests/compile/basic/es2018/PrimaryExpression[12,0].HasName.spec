          1. Let _expr_ be CoveredParenthesizedExpression of |CoverParenthesizedExpressionAndArrowParameterList|.
          1. If IsFunctionDefinition of _expr_ is *false*, return *false*.
          1. Return HasName of _expr_.