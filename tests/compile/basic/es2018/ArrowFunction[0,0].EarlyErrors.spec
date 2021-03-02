        <li>
          It is a Syntax Error if |ArrowParameters| Contains |YieldExpression| is *true*.
        </li>
        <li>
          It is a Syntax Error if |ArrowParameters| Contains |AwaitExpression| is *true*.
        </li>
        <li>
          It is a Syntax Error if ContainsUseStrict of |ConciseBody| is *true* and IsSimpleParameterList of |ArrowParameters| is *false*.
        </li>
        <li>
          It is a Syntax Error if any element of the BoundNames of |ArrowParameters| also occurs in the LexicallyDeclaredNames of |ConciseBody|.
        </li>