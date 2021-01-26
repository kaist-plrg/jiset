* It is a Syntax Error if ContainsUseStrict of |AsyncFunctionBody| is *true* and IsSimpleParameterList of |FormalParameters| is *false*.
* It is a Syntax Error if |FormalParameters| Contains |AwaitExpression| is *true*.
* If the source code matching this production is strict code, the Early Error rules for UniqueFormalParameters : FormalParameters are applied.
* If the source code matching this production is strict code, it is a Syntax Error if |BindingIdentifier| is present and the StringValue of |BindingIdentifier| is `"eval"` or `"arguments"`.
* It is a Syntax Error if any element of the BoundNames of |FormalParameters| also occurs in the LexicallyDeclaredNames of |AsyncFunctionBody|.
* It is a Syntax Error if |FormalParameters| Contains |SuperProperty| is *true*.
* It is a Syntax Error if |AsyncFunctionBody| Contains |SuperProperty| is *true*.
* It is a Syntax Error if |FormalParameters| Contains |SuperCall| is *true*.
* It is a Syntax Error if |AsyncFunctionBody| Contains |SuperCall| is *true*.