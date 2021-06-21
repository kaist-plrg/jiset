        1. Perform ? FunctionDeclarationInstantiation(_functionObject_, _argumentsList_).
        1. Let _generator_ be ? OrdinaryCreateFromConstructor(_functionObject_, *"%AsyncGeneratorFunction.prototype.prototype%"*, « [[AsyncGeneratorState]], [[AsyncGeneratorContext]], [[AsyncGeneratorQueue]], [[GeneratorBrand]] »).
        1. Set _generator_.[[GeneratorBrand]] to ~empty~.
        1. Perform ! AsyncGeneratorStart(_generator_, |FunctionBody|).
        1. Return Completion { [[Type]]: ~return~, [[Value]]: _generator_, [[Target]]: ~empty~ }.