          1. If the first |Expression| is present, then
            1. Let _exprRef_ be the result of evaluating the first |Expression|.
            1. Perform ? GetValue(_exprRef_).
          1. Return ? ForBodyEvaluation(the second |Expression|, the third |Expression|, |Statement|, « », _labelSet_).