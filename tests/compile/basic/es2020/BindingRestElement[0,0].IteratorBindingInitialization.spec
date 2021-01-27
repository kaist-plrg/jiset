          1. Let _lhs_ be ? ResolveBinding(StringValue of |BindingIdentifier|, _environment_).
          1. Let _A_ be ! ArrayCreate(0).
          1. Let _n_ be 0.
          1. Repeat,
            1. If _iteratorRecord_.[[Done]] is *false*, then
              1. Let _next_ be IteratorStep(_iteratorRecord_).
              1. If _next_ is an abrupt completion, set _iteratorRecord_.[[Done]] to *true*.
              1. ReturnIfAbrupt(_next_).
              1. If _next_ is *false*, set _iteratorRecord_.[[Done]] to *true*.
            1. If _iteratorRecord_.[[Done]] is *true*, then
              1. If _environment_ is *undefined*, return ? PutValue(_lhs_, _A_).
              1. Return InitializeReferencedBinding(_lhs_, _A_).
            1. Let _nextValue_ be IteratorValue(_next_).
            1. If _nextValue_ is an abrupt completion, set _iteratorRecord_.[[Done]] to *true*.
            1. ReturnIfAbrupt(_nextValue_).
            1. Perform ! CreateDataPropertyOrThrow(_A_, ! ToString(_n_), _nextValue_).
            1. Set _n_ to _n_ + 1.