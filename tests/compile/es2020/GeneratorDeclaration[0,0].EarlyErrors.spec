* If the source code matching |FormalParameters| is strict mode code, the Early Error rules for UniqueFormalParameters : FormalParameters are applied.
* If |BindingIdentifier| is present and the source code matching |BindingIdentifier| is strict mode code, it is a Syntax Error if the StringValue of |BindingIdentifier| is *"eval"* or *"arguments"*.
* It is a Syntax Error if ContainsUseStrict of |GeneratorBody| is *true* and IsSimpleParameterList of |FormalParameters| is *false*.
* It is a Syntax Error if any element of the BoundNames of |FormalParameters| also occurs in the LexicallyDeclaredNames of |GeneratorBody|.
* It is a Syntax Error if |FormalParameters| Contains |YieldExpression| is *true*.
* It is a Syntax Error if |FormalParameters| Contains |SuperProperty| is *true*.
* It is a Syntax Error if |GeneratorBody| Contains |SuperProperty| is *true*.
* It is a Syntax Error if |FormalParameters| Contains |SuperCall| is *true*.
* It is a Syntax Error if |GeneratorBody| Contains |SuperCall| is *true*.