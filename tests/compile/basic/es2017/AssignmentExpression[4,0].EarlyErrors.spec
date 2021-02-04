        <li>
          It is a Syntax Error if |LeftHandSideExpression| is either an |ObjectLiteral| or an |ArrayLiteral| and the lexical token sequence matched by |LeftHandSideExpression| cannot be parsed with no tokens left over using |AssignmentPattern| as the goal symbol.
        </li>
        <li>
          It is an early Reference Error if |LeftHandSideExpression| is neither an |ObjectLiteral| nor an |ArrayLiteral| and IsValidSimpleAssignmentTarget of |LeftHandSideExpression| is *false*.
        </li>