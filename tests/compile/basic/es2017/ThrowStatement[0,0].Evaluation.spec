        1. Let _exprRef_ be the result of evaluating |Expression|.
        1. Let _exprValue_ be ? GetValue(_exprRef_).
        1. Return Completion{[[Type]]: ~throw~, [[Value]]: _exprValue_, [[Target]]: ~empty~}.