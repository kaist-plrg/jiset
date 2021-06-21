        1. For each child node _child_ of this Parse Node, do
          1. If _child_ is an instance of _symbol_, return *true*.
          1. If _child_ is an instance of a nonterminal, then
            1. Let _contained_ be the result of _child_ Contains _symbol_.
            1. If _contained_ is *true*, return *true*.
        1. Return *false*.