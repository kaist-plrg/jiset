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
          1. Let _defaultValue_ be the result of evaluating |Initializer|.
          1. Set _v_ to ? GetValue(_defaultValue_).
        1. Return the result of performing BindingInitialization of |BindingPattern| with _v_ and _environment_ as the arguments.