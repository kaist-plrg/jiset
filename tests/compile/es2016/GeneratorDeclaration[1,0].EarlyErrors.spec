* If the source code matching this production is strict code, the Early Error rules for StrictFormalParameters : FormalParameters are applied.
* If the source code matching this production is strict code, it is a Syntax Error if |BindingIdentifier| is the |IdentifierName| `eval` or the |IdentifierName| `arguments`.
* It is a Syntax Error if ContainsUseStrict of |GeneratorBody| is *true* and IsSimpleParameterList of |FormalParameters| is *false*.
* It is a Syntax Error if any element of the BoundNames of |FormalParameters| also occurs in the LexicallyDeclaredNames of |GeneratorBody|.
* It is a Syntax Error if |FormalParameters| Contains |YieldExpression| is *true*.
* It is a Syntax Error if |FormalParameters| Contains |SuperProperty| is *true*.
* It is a Syntax Error if |GeneratorBody| Contains |SuperProperty| is *true*.
* It is a Syntax Error if |FormalParameters| Contains |SuperCall| is *true*.
* It is a Syntax Error if |GeneratorBody| Contains |SuperCall| is *true*.