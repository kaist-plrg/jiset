          1. Let _preceding_ be ? SubstitutionEvaluation of |TemplateMiddleList|.
          1. Let _nextRef_ be the result of evaluating |Expression|.
          1. Let _next_ be ? GetValue(_nextRef_).
          1. Append _next_ as the last element of the List _preceding_.
          1. Return _preceding_.