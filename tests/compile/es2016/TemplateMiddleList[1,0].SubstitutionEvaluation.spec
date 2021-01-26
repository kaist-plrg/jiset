          1. Let _preceding_ be the result of SubstitutionEvaluation of |TemplateMiddleList|.
          1. ReturnIfAbrupt(_preceding_).
          1. Let _next_ be the result of evaluating |Expression|.
          1. ReturnIfAbrupt(_next_).
          1. Append _next_ as the last element of the List _preceding_.
          1. Return _preceding_.