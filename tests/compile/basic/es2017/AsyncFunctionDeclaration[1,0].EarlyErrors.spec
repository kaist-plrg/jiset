        <li>It is a Syntax Error if ContainsUseStrict of |AsyncFunctionBody| is *true* and IsSimpleParameterList of |FormalParameters| is *false*.</li>
        <li>It is a Syntax Error if |FormalParameters| Contains |AwaitExpression| is *true*.</li>
        <li>If the source code matching this production is strict code, the Early Error rules for <emu-grammar>UniqueFormalParameters : FormalParameters</emu-grammar> are applied.</li>
        <li>If the source code matching this production is strict code, it is a Syntax Error if |BindingIdentifier| is the |IdentifierName| `eval` or the |IdentifierName| `arguments`.</li>
        <li>It is a Syntax Error if any element of the BoundNames of |FormalParameters| also occurs in the LexicallyDeclaredNames of |AsyncFunctionBody|.</li>
        <li>It is a Syntax Error if |FormalParameters| Contains |SuperProperty| is *true*.</li>
        <li>It is a Syntax Error if |AsyncFunctionBody| Contains |SuperProperty| is *true*.</li>
        <li>It is a Syntax Error if |FormalParameters| Contains |SuperCall| is *true*.</li>
        <li>It is a Syntax Error if |AsyncFunctionBody| Contains |SuperCall| is *true*.</li>