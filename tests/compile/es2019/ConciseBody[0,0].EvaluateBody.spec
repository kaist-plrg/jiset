        1. Perform ? FunctionDeclarationInstantiation(_functionObject_, _argumentsList_).
        1. Let _exprRef_ be the result of evaluating |AssignmentExpression|.
        1. Let _exprValue_ be ? GetValue(_exprRef_).
        1. Return Completion { [[Type]]: ~return~, [[Value]]: _exprValue_, [[Target]]: ~empty~ }.