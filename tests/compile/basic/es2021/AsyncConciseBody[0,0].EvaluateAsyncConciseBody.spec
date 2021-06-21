        1. Let _promiseCapability_ be ! NewPromiseCapability(%Promise%).
        1. Let _declResult_ be FunctionDeclarationInstantiation(_functionObject_, _argumentsList_).
        1. If _declResult_ is not an abrupt completion, then
          1. Perform ! AsyncFunctionStart(_promiseCapability_, |ExpressionBody|).
        1. Else,
          1. Perform ! Call(_promiseCapability_.[[Reject]], *undefined*, « _declResult_.[[Value]] »).
        1. Return Completion { [[Type]]: ~return~, [[Value]]: _promiseCapability_.[[Promise]], [[Target]]: ~empty~ }.