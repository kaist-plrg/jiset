          1. Let _templateLiteral_ be this |TemplateLiteral|.
          1. Let _siteObj_ be GetTemplateObject(_templateLiteral_).
          1. Let _remaining_ be ? ArgumentListEvaluation of |SubstitutionTemplate|.
          1. Return a List whose first element is _siteObj_ and whose subsequent elements are the elements of _remaining_.