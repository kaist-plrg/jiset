        <li>
          If the source code matching |FormalParameters| is strict mode code, the Early Error rules for <emu-grammar>UniqueFormalParameters : FormalParameters</emu-grammar> are applied.
        </li>
        <li>
          If |BindingIdentifier| is present and the source code matching |BindingIdentifier| is strict mode code, it is a Syntax Error if the StringValue of |BindingIdentifier| is *"eval"* or *"arguments"*.
        </li>
        <li>
          It is a Syntax Error if ContainsUseStrict of |GeneratorBody| is *true* and IsSimpleParameterList of |FormalParameters| is *false*.
        </li>
        <li>
          It is a Syntax Error if any element of the BoundNames of |FormalParameters| also occurs in the LexicallyDeclaredNames of |GeneratorBody|.
        </li>
        <li>
          It is a Syntax Error if |FormalParameters| Contains |YieldExpression| is *true*.
        </li>
        <li>
          It is a Syntax Error if |FormalParameters| Contains |SuperProperty| is *true*.
        </li>
        <li>
          It is a Syntax Error if |GeneratorBody| Contains |SuperProperty| is *true*.
        </li>
        <li>
          It is a Syntax Error if |FormalParameters| Contains |SuperCall| is *true*.
        </li>
        <li>
          It is a Syntax Error if |GeneratorBody| Contains |SuperCall| is *true*.
        </li>