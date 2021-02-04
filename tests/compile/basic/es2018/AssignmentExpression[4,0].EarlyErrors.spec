        <li>
          It is a Syntax Error if |LeftHandSideExpression| is either an |ObjectLiteral| or an |ArrayLiteral| and |LeftHandSideExpression| is not covering an |AssignmentPattern|.
        </li>
        <li>
          It is an early Reference Error if |LeftHandSideExpression| is neither an |ObjectLiteral| nor an |ArrayLiteral| and IsValidSimpleAssignmentTarget of |LeftHandSideExpression| is *false*.
        </li>