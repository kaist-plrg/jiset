          1. Let _varDcl_ be the result of evaluating |VariableDeclarationList|.
          1. ReturnIfAbrupt(_varDcl_).
          1. Return ? ForBodyEvaluation(the first |Expression|, the second |Expression|, |Statement|, « », _labelSet_).