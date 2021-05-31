        1. Let _lref_ be the result of evaluating |LeftHandSideExpression|.
        1. [id="step-assignmentexpression-evaluation-lgcl-nullish-getvalue"] Let _lval_ be ? GetValue(_lref_).
        1. If _lval_ is neither *undefined* nor *null*, return _lval_.
        1. If IsAnonymousFunctionDefinition(|AssignmentExpression|) is *true* and IsIdentifierRef of |LeftHandSideExpression| is *true*, then
          1. Let _rval_ be NamedEvaluation of |AssignmentExpression| with argument _lref_.[[ReferencedName]].
        1. Else,
          1. Let _rref_ be the result of evaluating |AssignmentExpression|.
          1. Let _rval_ be ? GetValue(_rref_).
        1. [id="step-assignmentexpression-evaluation-lgcl-nullish-putvalue"] Perform ? PutValue(_lref_, _rval_).
        1. Return _rval_.