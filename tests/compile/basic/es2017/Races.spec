      1. Let happens-before be _execution_.[[HappensBefore]].
      1. Let reads-from be _execution_.[[ReadsFrom]].
      1. _E_ is not _D_, and
      1. It is not the case that _E_ happens-before _D_ or _D_ happens-before _E_, and
      1. If _E_ and _D_ are both WriteSharedMemory or ReadModifyWriteSharedMemory events, then
        1. _E_ and _D_ do not have disjoint ranges.
      1. Else,
        1. _E_ reads-from _D_ or _D_ reads-from _E_.