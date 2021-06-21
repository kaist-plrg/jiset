          1. Let _generatorKind_ be ! GetGeneratorKind().
          1. If _generatorKind_ is ~async~, return ? AsyncGeneratorYield(_value_).
          1. Otherwise, return ? GeneratorYield(! CreateIterResultObject(_value_, *false*)).