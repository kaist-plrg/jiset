          1. Let _iteratorRecord_ be ? GetIterator(_value_).
          1. Let _result_ be IteratorBindingInitialization for |ArrayBindingPattern| using _iteratorRecord_ and _environment_ as arguments.
          1. If _iteratorRecord_.[[Done]] is *false*, return ? IteratorClose(_iteratorRecord_, _result_).
          1. Return _result_.