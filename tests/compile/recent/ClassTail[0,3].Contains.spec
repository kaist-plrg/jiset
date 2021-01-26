        1. If _symbol_ is |ClassBody|, return *true*.
        1. If _symbol_ is |ClassHeritage|, then
          1. If |ClassHeritage| is present, return *true*; otherwise return *false*.
        1. Let _inHeritage_ be |ClassHeritage| Contains _symbol_.
        1. If _inHeritage_ is *true*, return *true*.
        1. Return the result of ComputedPropertyContains for |ClassBody| with argument _symbol_.