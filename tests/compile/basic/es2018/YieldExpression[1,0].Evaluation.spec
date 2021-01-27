        1. Let _generatorKind_ be ! GetGeneratorKind().
        1. Let _exprRef_ be the result of evaluating |AssignmentExpression|.
        1. Let _value_ be ? GetValue(_exprRef_).
        1. If _generatorKind_ is ~async~, then return ? AsyncGeneratorYield(_value_).
        1. Otherwise, return ? GeneratorYield(CreateIterResultObject(_value_, *false*)).