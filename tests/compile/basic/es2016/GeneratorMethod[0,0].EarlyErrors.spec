* It is a Syntax Error if HasDirectSuper of |GeneratorMethod| is *true*.
* It is a Syntax Error if |StrictFormalParameters| Contains |YieldExpression| is *true*.
* It is a Syntax Error if ContainsUseStrict of |GeneratorBody| is *true* and IsSimpleParameterList of |StrictFormalParameters| is *false*.
* It is a Syntax Error if any element of the BoundNames of |StrictFormalParameters| also occurs in the LexicallyDeclaredNames of |GeneratorBody|.