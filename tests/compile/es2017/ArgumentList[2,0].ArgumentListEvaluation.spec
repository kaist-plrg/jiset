          1. Let _precedingArgs_ be ArgumentListEvaluation of |ArgumentList|.
          1. ReturnIfAbrupt(_precedingArgs_).
          1. Let _ref_ be the result of evaluating |AssignmentExpression|.
          1. Let _arg_ be ? GetValue(_ref_).
          1. Append _arg_ to the end of _precedingArgs_.
          1. Return _precedingArgs_.