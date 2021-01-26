        1. Let _result_ be ? IteratorNext(_iterator_).
        1. Let _done_ be ? IteratorComplete(_result_).
        1. If _done_ is *true*, return *false*.
        1. Return _result_.