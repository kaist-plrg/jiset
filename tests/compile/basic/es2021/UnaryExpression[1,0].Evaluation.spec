          1. Let _ref_ be the result of evaluating |UnaryExpression|.
          1. ReturnIfAbrupt(_ref_).
          1. If _ref_ is not a Reference Record, return *true*.
          1. If IsUnresolvableReference(_ref_) is *true*, then
            1. Assert: _ref_.[[Strict]] is *false*.
            1. Return *true*.
          1. If IsPropertyReference(_ref_) is *true*, then
            1. If IsSuperReference(_ref_) is *true*, throw a *ReferenceError* exception.
            1. [id="step-delete-operator-toobject"] Let _baseObj_ be ! ToObject(_ref_.[[Base]]).
            1. Let _deleteStatus_ be ? _baseObj_.[[Delete]](_ref_.[[ReferencedName]]).
            1. If _deleteStatus_ is *false* and _ref_.[[Strict]] is *true*, throw a *TypeError* exception.
            1. Return _deleteStatus_.
          1. Else,
            1. Let _base_ be _ref_.[[Base]].
            1. Assert: _base_ is an Environment Record.
            1. Return ? _base_.DeleteBinding(_ref_.[[ReferencedName]]).