        1. If |Statement| is either a |LabelledStatement| or a |BreakableStatement|, then
          1. Return LabelledEvaluation of |Statement| with argument _labelSet_.
        1. Else,
          1. Return the result of evaluating |Statement|.