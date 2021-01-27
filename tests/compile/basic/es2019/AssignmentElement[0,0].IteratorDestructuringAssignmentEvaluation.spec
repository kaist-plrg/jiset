          1. If |DestructuringAssignmentTarget| is neither an |ObjectLiteral| nor an |ArrayLiteral|, then
            1. Let _lref_ be the result of evaluating |DestructuringAssignmentTarget|.
            1. ReturnIfAbrupt(_lref_).
          1. If _iteratorRecord_.[[Done]] is *false*, then
            1. Let _next_ be IteratorStep(_iteratorRecord_).
            1. If _next_ is an abrupt completion, set _iteratorRecord_.[[Done]] to *true*.
            1. ReturnIfAbrupt(_next_).
            1. If _next_ is *false*, set _iteratorRecord_.[[Done]] to *true*.
            1. Else,
              1. Let _value_ be IteratorValue(_next_).
              1. If _value_ is an abrupt completion, set _iteratorRecord_.[[Done]] to *true*.
              1. ReturnIfAbrupt(_value_).
          1. If _iteratorRecord_.[[Done]] is *true*, let _value_ be *undefined*.
          1. If |Initializer| is present and _value_ is *undefined*, then
            1. If IsAnonymousFunctionDefinition(|Initializer|) and IsIdentifierRef of |DestructuringAssignmentTarget| are both *true*, then
              1. Let _v_ be the result of performing NamedEvaluation for |Initializer| with argument GetReferencedName(_lref_).
            1. Else,
              1. Let _defaultValue_ be the result of evaluating |Initializer|.
              1. Let _v_ be ? GetValue(_defaultValue_).
          1. Else, let _v_ be _value_.
          1. If |DestructuringAssignmentTarget| is an |ObjectLiteral| or an |ArrayLiteral|, then
            1. Let _nestedAssignmentPattern_ be the |AssignmentPattern| that is covered by |DestructuringAssignmentTarget|.
            1. Return the result of performing DestructuringAssignmentEvaluation of _nestedAssignmentPattern_ with _v_ as the argument.
          1. Return ? PutValue(_lref_, _v_).