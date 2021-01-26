        1. For each ReadSharedMemory or ReadModifyWriteSharedMemory event _R_ in SharedDataBlockEventSet(_execution_), do
          1. Let _Ws_ be _execution_.[[ReadsBytesFrom]](_R_).
          1. Let _byteLocation_ be _R_.[[ByteIndex]].
          1. For each element _W_ of _Ws_ in List order, do
            1. If (_R_, _W_) is in _execution_.[[HappensBefore]], then
              1. Return *false*.
            1. If there is a WriteSharedMemory or ReadModifyWriteSharedMemory event _V_ that has _byteLocation_ in its range such that the pairs (_W_, _V_) and (_V_, _R_) are in _execution_.[[HappensBefore]], then
              1. Return *false*.
            1. Set _byteLocation_ to _byteLocation_ + 1.
        1. Return *true*.