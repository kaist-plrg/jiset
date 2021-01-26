* It is a Syntax Error if |CoverCallExpressionAndAsyncArrowHead| Contains |YieldExpression| is *true*.
* It is a Syntax Error if |CoverCallExpressionAndAsyncArrowHead| Contains |AwaitExpression| is *true*.
* It is a Syntax Error if the lexical token sequence matched by |CoverCallExpressionAndAsyncArrowHead| cannot be parsed with no tokens left over using |AsyncArrowHead| as the goal symbol.
* It is a Syntax Error if any element of the BoundNames of |CoverCallExpressionAndAsyncArrowHead| also occurs in the LexicallyDeclaredNames of |AsyncConciseBody|.
* It is a Syntax Error if ContainsUseStrict of |AsyncConciseBody| is *true* and IsSimpleParameterList of |CoverCallExpressionAndAsyncArrowHead| is *false*.
* All Early Error rules for |AsyncArrowHead| and its derived productions apply to CoveredAsyncArrowHead of |CoverCallExpressionAndAsyncArrowHead|.