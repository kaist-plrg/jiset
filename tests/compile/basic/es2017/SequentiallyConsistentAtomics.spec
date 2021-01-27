        1. Let happens-before be _execution_.[[HappensBefore]].
        1. Let synchronizes-with be _execution_.[[SynchronizesWith]].
        1. For each pair of events _E_ and _D_ in EventSet(_execution_), do
          1. If _E_ happens-before _D_, then _E_ is memory-order before _D_.
          1. If _E_ and _D_ are in SharedDataBlockEventSet(_execution_) and _E_ synchronizes-with _D_, then
            1. Assert: _D_.[[Order]] is `"SeqCst"`.
            1. There is no WriteSharedMemory or ReadModifyWriteSharedMemory event _W_ in SharedDataBlockEventSet(_execution_) with equal range as _D_ such that _W_ is not _E_, _E_ is memory-order before _W_, and _W_ is memory-order before _D_.
            1. NOTE: This clause additionally constrains `"SeqCst"` events on equal ranges.
        1. For each WriteSharedMemory or ReadModifyWriteSharedMemory event _W_ in SharedDataBlockEventSet(_execution_), do
          1. If _W_.[[Order]] is `"SeqCst"`, then it is not the case that there is an infinite number of ReadSharedMemory or ReadModifyWriteSharedMemory events in SharedDataBlockEventSet(_execution_) with equal range that is memory-order before _W_.
          1. NOTE: This clause together with the forward progress guarantee on agents ensure the liveness condition that `"SeqCst"` writes become visible to `"SeqCst"` reads with equal range in finite time.