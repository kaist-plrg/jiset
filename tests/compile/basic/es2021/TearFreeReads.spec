        1. For each ReadSharedMemory or ReadModifyWriteSharedMemory event _R_ of SharedDataBlockEventSet(_execution_), do
          1. If _R_.[[NoTear]] is *true*, then
            1. Assert: The remainder of dividing _R_.[[ByteIndex]] by _R_.[[ElementSize]] is 0.
            1. For each event _W_ such that (_R_, _W_) is in _execution_.[[ReadsFrom]] and _W_.[[NoTear]] is *true*, do
              1. If _R_ and _W_ have equal ranges, and there is an event _V_ such that _V_ and _W_ have equal ranges, _V_.[[NoTear]] is *true*, _W_ is not _V_, and (_R_, _V_) is in _execution_.[[ReadsFrom]], then
                1. Return *false*.
        1. Return *true*.