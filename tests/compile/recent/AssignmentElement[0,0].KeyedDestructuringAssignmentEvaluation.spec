          1. If |DestructuringAssignmentTarget| is neither an |ObjectLiteral| nor an |ArrayLiteral|, then
            1. Let _lref_ be the result of evaluating |DestructuringAssignmentTarget|.
            1. ReturnIfAbrupt(_lref_).
          1. Let _v_ be ? GetV(_value_, _propertyName_).
          1. If |Initializer| is present and _v_ is *undefined*, then
            1. If IsAnonymousFunctionDefinition(|Initializer|) and IsIdentifierRef of |DestructuringAssignmentTarget| are both *true*, then
              1. Let _rhsValue_ be ? NamedEvaluation of |Initializer| with argument _lref_.[[ReferencedName]].
            1. Else,
              1. Let _defaultValue_ be the result of evaluating |Initializer|.
              1. Let _rhsValue_ be ? GetValue(_defaultValue_).
          1. Else, let _rhsValue_ be _v_.
          1. If |DestructuringAssignmentTarget| is an |ObjectLiteral| or an |ArrayLiteral|, then
            1. Let _assignmentPattern_ be the |AssignmentPattern| that is covered by |DestructuringAssignmentTarget|.
            1. Return the result of performing DestructuringAssignmentEvaluation of _assignmentPattern_ with _rhsValue_ as the argument.
          1. Return ? PutValue(_lref_, _rhsValue_).