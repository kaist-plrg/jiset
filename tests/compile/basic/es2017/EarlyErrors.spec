          <li>
            It is a Syntax Error if IsValidSimpleAssignmentTarget of |LeftHandSideExpression| is *false*.
          </li>
          <li>
            It is a Syntax Error if the |LeftHandSideExpression| is <emu-grammar>CoverParenthesizedExpressionAndArrowParameterList : `(` Expression `)`</emu-grammar> and |Expression| derives a phrase that would produce a Syntax Error according to these rules if that phrase were substituted for |LeftHandSideExpression|. This rule is recursively applied.
          </li>