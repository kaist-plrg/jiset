          1. Let _firstSubRef_ be the result of evaluating |Expression|.
          1. Let _firstSub_ be ? GetValue(_firstSubRef_).
          1. Let _restSub_ be ? SubstitutionEvaluation of |TemplateSpans|.
          1. Assert: _restSub_ is a List.
          1. Return a List whose first element is _firstSub_ and whose subsequent elements are the elements of _restSub_. _restSub_ may contain no elements.