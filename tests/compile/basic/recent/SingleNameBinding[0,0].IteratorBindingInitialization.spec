          1. Let _bindingId_ be StringValue of |BindingIdentifier|.
          1. Let _lhs_ be ? ResolveBinding(_bindingId_, _environment_).
          1. If _iteratorRecord_.[[Done]] is *false*, then
            1. Let _next_ be IteratorStep(_iteratorRecord_).
            1. If _next_ is an abrupt completion, set _iteratorRecord_.[[Done]] to *true*.
            1. ReturnIfAbrupt(_next_).
            1. If _next_ is *false*, set _iteratorRecord_.[[Done]] to *true*.
            1. Else,
              1. Let _v_ be IteratorValue(_next_).
              1. If _v_ is an abrupt completion, set _iteratorRecord_.[[Done]] to *true*.
              1. ReturnIfAbrupt(_v_).
          1. If _iteratorRecord_.[[Done]] is *true*, let _v_ be *undefined*.
          1. If |Initializer| is present and _v_ is *undefined*, then
            1. If IsAnonymousFunctionDefinition(|Initializer|) is *true*, then
              1. Set _v_ to the result of performing NamedEvaluation for |Initializer| with argument _bindingId_.
            1. Else,
              1. Let _defaultValue_ be the result of evaluating |Initializer|.
              1. Set _v_ to ? GetValue(_defaultValue_).
          1. If _environment_ is *undefined*, return ? PutValue(_lhs_, _v_).
          1. Return InitializeReferencedBinding(_lhs_, _v_).