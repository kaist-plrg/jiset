          1. Let _templateLiteral_ be this |TemplateLiteral|.
          1. Let _siteObj_ be GetTemplateObject(_templateLiteral_).
          1. Let _firstSub_ be the result of evaluating |Expression|.
          1. ReturnIfAbrupt(_firstSub_).
          1. Let _restSub_ be SubstitutionEvaluation of |TemplateSpans|.
          1. ReturnIfAbrupt(_restSub_).
          1. Assert: _restSub_ is a List.
          1. Return a List whose first element is _siteObj_, whose second elements is _firstSub_, and whose subsequent elements are the elements of _restSub_, in order. _restSub_ may contain no elements.