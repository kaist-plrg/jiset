        <li>It is a Syntax Error if HasDirectSuper of |AsyncGeneratorMethod| is *true*.</li>
        <li>It is a Syntax Error if |UniqueFormalParameters| Contains |YieldExpression| is *true*.</li>
        <li>It is a Syntax Error if |UniqueFormalParameters| Contains |AwaitExpression| is *true*.</li>
        <li>It is a Syntax Error if ContainsUseStrict of |AsyncGeneratorBody| is *true* and IsSimpleParameterList of |UniqueFormalParameters| is *false*.</li>
        <li>It is a Syntax Error if any element of the BoundNames of |UniqueFormalParameters| also occurs in the LexicallyDeclaredNames of |AsyncGeneratorBody|.</li>