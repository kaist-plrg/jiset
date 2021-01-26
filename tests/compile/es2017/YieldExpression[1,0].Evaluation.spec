        1. Let _exprRef_ be the result of evaluating |AssignmentExpression|.
        1. Let _value_ be ? GetValue(_exprRef_).
        1. Return ? GeneratorYield(CreateIterResultObject(_value_, *false*)).