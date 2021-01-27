        1. Let reads-from be _execution_.[[ReadsFrom]].
        1. For each ReadSharedMemory or ReadModifyWriteSharedMemory event _R_ in SharedDataBlockEventSet(_execution_), do
          1. If _R_.[[NoTear]] is *true*, then
            1. Assert: The remainder of dividing _R_.[[ByteIndex]] by _R_.[[ElementSize]] is 0.
            1. For each event _W_ such that _R_ reads-from _W_ and _W_.[[NoTear]] is *true*, do
              1. If _R_ and _W_ have equal ranges, then there is no _V_ such that _V_ and _W_ have equal range, _V_.[[NoTear]] is *true*, _W_ is not _V_, and _R_ reads-from _V_.