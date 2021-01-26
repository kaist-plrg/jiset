        1. Let happens-before be _execution_.[[HappensBefore]].
        1. Let reads-bytes-from be _execution_.[[ReadsBytesFrom]].
        1. For each ReadSharedMemory or ReadModifyWriteSharedMemory event _R_ in SharedDataBlockEventSet(_execution_), do
          1. Let _Ws_ be the List of events reads-bytes-from(_R_).
          1. Let _byteLocation_ be _R_.[[ByteIndex]].
          1. For each element _W_ of _Ws_ in List order, do
            1. It is not the case that _R_ happens-before _W_, and
            1. There is no WriteSharedMemory or ReadModifyWriteSharedMemory event _V_ that has _byteLocation_ in its range such that _W_ happens-before _V_ and _V_ happens-before _R_.
            1. Increment _byteLocation_ by 1.