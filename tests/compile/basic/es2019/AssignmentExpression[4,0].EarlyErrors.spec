        <li>
          It is a Syntax Error if |LeftHandSideExpression| is either an |ObjectLiteral| or an |ArrayLiteral| and |LeftHandSideExpression| is not covering an |AssignmentPattern|.
        </li>
        <li>
          It is an early Reference Error if |LeftHandSideExpression| is neither an |ObjectLiteral| nor an |ArrayLiteral| and AssignmentTargetType of |LeftHandSideExpression| is ~invalid~.
        </li>
        <li>
          It is an early Syntax Error if |LeftHandSideExpression| is neither an |ObjectLiteral| nor an |ArrayLiteral| and AssignmentTargetType of |LeftHandSideExpression| is ~strict~.
        </li>