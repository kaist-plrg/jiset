        1. Let _n_ be ? ToInteger(_size_).
        1. Let _AR_ be the Agent Record of the surrounding agent.
        1. If _n_ equals 1, return _AR_.[[IsLockFree1]].
        1. If _n_ equals 2, return _AR_.[[IsLockFree2]].
        1. If _n_ equals 4, return *true*.
        1. If _n_ equals 8, return _AR_.[[IsLockFree8]].
        1. Return *false*.