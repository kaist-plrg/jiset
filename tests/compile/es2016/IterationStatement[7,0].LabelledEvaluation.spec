          1. Let _keyResult_ be the result of performing ? ForIn/OfHeadEvaluation(BoundNames of |ForDeclaration|, |Expression|, ~enumerate~).
          1. Return ? ForIn/OfBodyEvaluation(|ForDeclaration|, |Statement|, _keyResult_, ~lexicalBinding~, _labelSet_).