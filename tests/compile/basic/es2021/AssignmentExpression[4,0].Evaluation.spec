        1. If |LeftHandSideExpression| is neither an |ObjectLiteral| nor an |ArrayLiteral|, then
          1. Let _lref_ be the result of evaluating |LeftHandSideExpression|.
          1. ReturnIfAbrupt(_lref_).
          1. If IsAnonymousFunctionDefinition(|AssignmentExpression|) and IsIdentifierRef of |LeftHandSideExpression| are both *true*, then
            1. Let _rval_ be NamedEvaluation of |AssignmentExpression| with argument _lref_.[[ReferencedName]].
          1. Else,
            1. Let _rref_ be the result of evaluating |AssignmentExpression|.
            1. Let _rval_ be ? GetValue(_rref_).
          1. [id="step-assignmentexpression-evaluation-simple-putvalue"] Perform ? PutValue(_lref_, _rval_).
          1. Return _rval_.
        1. Let _assignmentPattern_ be the |AssignmentPattern| that is covered by |LeftHandSideExpression|.
        1. Let _rref_ be the result of evaluating |AssignmentExpression|.
        1. Let _rval_ be ? GetValue(_rref_).
        1. Perform ? DestructuringAssignmentEvaluation of _assignmentPattern_ using _rval_ as the argument.
        1. Return _rval_.