        1. Perform ? FunctionDeclarationInstantiation(_functionObject_, _argumentsList_).
        1. Let _G_ be ? OrdinaryCreateFromConstructor(_functionObject_, *"%GeneratorFunction.prototype.prototype%"*, « [[GeneratorState]], [[GeneratorContext]], [[GeneratorBrand]] »).
        1. Set _G_.[[GeneratorBrand]] to ~empty~.
        1. Perform GeneratorStart(_G_, |FunctionBody|).
        1. Return Completion { [[Type]]: ~return~, [[Value]]: _G_, [[Target]]: ~empty~ }.