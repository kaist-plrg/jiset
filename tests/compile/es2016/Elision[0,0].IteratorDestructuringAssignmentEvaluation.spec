          1. If _iteratorRecord_.[[Done]] is *false*, then
            1. Let _next_ be IteratorStep(_iteratorRecord_.[[Iterator]]).
            1. If _next_ is an abrupt completion, set _iteratorRecord_.[[Done]] to *true*.
            1. ReturnIfAbrupt(_next_).
            1. If _next_ is *false*, set _iteratorRecord_.[[Done]] to *true*.
          1. Return NormalCompletion(~empty~).