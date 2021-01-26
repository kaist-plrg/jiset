        1. Assert: Type(_iteratorRecord_.[[Iterator]]) is Object.
        1. Assert: _completion_ is a Completion Record.
        1. Let _iterator_ be _iteratorRecord_.[[Iterator]].
        1. Let _return_ be ? GetMethod(_iterator_, *"return"*).
        1. If _return_ is *undefined*, return Completion(_completion_).
        1. Let _innerResult_ be Call(_return_, _iterator_).
        1. If _completion_.[[Type]] is ~throw~, return Completion(_completion_).
        1. If _innerResult_.[[Type]] is ~throw~, return Completion(_innerResult_).
        1. If Type(_innerResult_.[[Value]]) is not Object, throw a *TypeError* exception.
        1. Return Completion(_completion_).