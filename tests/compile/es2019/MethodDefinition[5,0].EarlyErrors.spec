* It is a Syntax Error if BoundNames of |PropertySetParameterList| contains any duplicate elements.
* It is a Syntax Error if ContainsUseStrict of |FunctionBody| is *true* and IsSimpleParameterList of |PropertySetParameterList| is *false*.
* It is a Syntax Error if any element of the BoundNames of |PropertySetParameterList| also occurs in the LexicallyDeclaredNames of |FunctionBody|.