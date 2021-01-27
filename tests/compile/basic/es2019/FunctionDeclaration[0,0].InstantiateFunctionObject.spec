        1. If the function code for |FunctionDeclaration| is strict mode code, let _strict_ be *true*. Otherwise let _strict_ be *false*.
        1. Let _name_ be StringValue of |BindingIdentifier|.
        1. Let _F_ be FunctionCreate(~Normal~, |FormalParameters|, |FunctionBody|, _scope_, _strict_).
        1. Perform MakeConstructor(_F_).
        1. Perform SetFunctionName(_F_, _name_).
        1. Set _F_.[[SourceText]] to the source text matched by |FunctionDeclaration|.
        1. Return _F_.