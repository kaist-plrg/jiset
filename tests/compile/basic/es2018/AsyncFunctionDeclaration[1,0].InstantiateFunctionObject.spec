        1. If the function code for |AsyncFunctionDeclaration| is strict mode code, let _strict_ be *true*. Otherwise, let _strict_ be *false*.
        1. Let _F_ be ! AsyncFunctionCreate(~Normal~, |FormalParameters|, |AsyncFunctionBody|, _scope_, _strict_).
        1. Perform ! SetFunctionName(_F_, `"default"`).
        1. Return _F_.