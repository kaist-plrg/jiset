        1. Let _exprRef_ be the result of evaluating |Expression|.
        1. Let _exprValue_ be ? GetValue(_exprRef_).
        1. If ! GetGeneratorKind() is ~async~, set _exprValue_ to ? Await(_exprValue_).
        1. Return Completion { [[Type]]: ~return~, [[Value]]: _exprValue_, [[Target]]: ~empty~ }.