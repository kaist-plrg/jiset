      1. If _E_ is not _D_, then
        1. If the pairs (_E_, _D_) and (_D_, _E_) are not in _execution_.[[HappensBefore]], then
          1. If _E_ and _D_ are both WriteSharedMemory or ReadModifyWriteSharedMemory events and _E_ and _D_ do not have disjoint ranges, then
            1. Return *true*.
          1. If either (_E_, _D_) or (_D_, _E_) is in _execution_.[[ReadsFrom]], then
            1. Return *true*.
      1. Return *false*.