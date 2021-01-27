          1. If |DestructuringAssignmentTarget| is neither an |ObjectLiteral| nor an |ArrayLiteral|, then
            1. Let _lref_ be the result of evaluating |DestructuringAssignmentTarget|.
            1. ReturnIfAbrupt(_lref_).
          1. Let _A_ be ! ArrayCreate(0).
          1. Let _n_ be 0.
          1. Repeat, while _iteratorRecord_.[[Done]] is *false*,
            1. Let _next_ be IteratorStep(_iteratorRecord_).
            1. If _next_ is an abrupt completion, set _iteratorRecord_.[[Done]] to *true*.
            1. ReturnIfAbrupt(_next_).
            1. If _next_ is *false*, set _iteratorRecord_.[[Done]] to *true*.
            1. Else,
              1. Let _nextValue_ be IteratorValue(_next_).
              1. If _nextValue_ is an abrupt completion, set _iteratorRecord_.[[Done]] to *true*.
              1. ReturnIfAbrupt(_nextValue_).
              1. Let _status_ be CreateDataProperty(_A_, ! ToString(_n_), _nextValue_).
              1. Assert: _status_ is *true*.
              1. Increment _n_ by 1.
          1. If |DestructuringAssignmentTarget| is neither an |ObjectLiteral| nor an |ArrayLiteral|, then
            1. Return ? PutValue(_lref_, _A_).
          1. Let _nestedAssignmentPattern_ be the |AssignmentPattern| that is covered by |DestructuringAssignmentTarget|.
          1. Return the result of performing DestructuringAssignmentEvaluation of _nestedAssignmentPattern_ with _A_ as the argument.