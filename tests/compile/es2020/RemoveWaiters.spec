          1. Assert: The calling agent is in the critical section for _WL_.
          1. Let _L_ be a new empty List.
          1. Let _S_ be a reference to the list of waiters in _WL_.
          1. Repeat, while _c_ > 0 and _S_ is not an empty List,
            1. Let _W_ be the first waiter in _S_.
            1. Add _W_ to the end of _L_.
            1. Remove _W_ from _S_.
            1. Set _c_ to _c_ - 1.
          1. Return _L_.