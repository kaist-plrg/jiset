* It is a Syntax Error if HasDirectSuper of |GeneratorMethod| is *true*.
* It is a Syntax Error if |UniqueFormalParameters| Contains |YieldExpression| is *true*.
* It is a Syntax Error if ContainsUseStrict of |GeneratorBody| is *true* and IsSimpleParameterList of |UniqueFormalParameters| is *false*.
* It is a Syntax Error if any element of the BoundNames of |UniqueFormalParameters| also occurs in the LexicallyDeclaredNames of |GeneratorBody|.