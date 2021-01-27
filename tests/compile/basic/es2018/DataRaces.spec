      1. If _E_ and _D_ are in a race in _execution_, then
        1. If _E_.[[Order]] is not `"SeqCst"` or _D_.[[Order]] is not `"SeqCst"`, then
          1. Return *true*.
        1. If _E_ and _D_ have overlapping ranges, then
          1. Return *true*.
      1. Return *false*.