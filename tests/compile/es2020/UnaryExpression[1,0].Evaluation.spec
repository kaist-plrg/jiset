          1. Let _ref_ be the result of evaluating |UnaryExpression|.
          1. ReturnIfAbrupt(_ref_).
          1. If Type(_ref_) is not Reference, return *true*.
          1. If IsUnresolvableReference(_ref_) is *true*, then
            1. Assert: IsStrictReference(_ref_) is *false*.
            1. Return *true*.
          1. If IsPropertyReference(_ref_) is *true*, then
            1. If IsSuperReference(_ref_) is *true*, throw a *ReferenceError* exception.
            1. Let _baseObj_ be ! ToObject(GetBase(_ref_)).
            1. Let _deleteStatus_ be ? _baseObj_.[[Delete]](GetReferencedName(_ref_)).
            1. If _deleteStatus_ is *false* and IsStrictReference(_ref_) is *true*, throw a *TypeError* exception.
            1. Return _deleteStatus_.
          1. Else,
            1. Assert: _ref_ is a Reference to an Environment Record binding.
            1. Let _bindings_ be GetBase(_ref_).
            1. Return ? _bindings_.DeleteBinding(GetReferencedName(_ref_)).