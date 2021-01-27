        1. Perform ? FunctionDeclarationInstantiation(_functionObject_, _argumentsList_).
        1. Let _generator_ be ? OrdinaryCreateFromConstructor(_functionObject_, *"%AsyncGenerator.prototype%"*, « [[AsyncGeneratorState]], [[AsyncGeneratorContext]], [[AsyncGeneratorQueue]] »).
        1. Perform ! AsyncGeneratorStart(_generator_, |FunctionBody|).
        1. Return Completion { [[Type]]: ~return~, [[Value]]: _generator_, [[Target]]: ~empty~ }.