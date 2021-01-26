        1. Let _generatorKind_ be ! GetGeneratorKind().
        1. If _generatorKind_ is ~async~, then return ? AsyncGeneratorYield(*undefined*).
        1. Otherwise, return ? GeneratorYield(CreateIterResultObject(*undefined*, *false*)).