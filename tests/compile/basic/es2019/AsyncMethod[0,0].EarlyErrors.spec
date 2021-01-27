* It is a Syntax Error if ContainsUseStrict of |AsyncFunctionBody| is *true* and IsSimpleParameterList of |UniqueFormalParameters| is *false*.
* It is a Syntax Error if HasDirectSuper of |AsyncMethod| is *true*.
* It is a Syntax Error if |UniqueFormalParameters| Contains |AwaitExpression| is *true*.
* It is a Syntax Error if any element of the BoundNames of |UniqueFormalParameters| also occurs in the LexicallyDeclaredNames of |AsyncFunctionBody|.