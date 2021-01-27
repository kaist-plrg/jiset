        1. Assert: _R_ is a ReadSharedMemory or ReadModifyWriteSharedMemory event.
        1. Let reads-bytes-from be _execution_.[[ReadsBytesFrom]].
        1. Let _Ws_ be reads-bytes-from(_R_).
        1. Assert: _Ws_ is a List of WriteSharedMemory or ReadModifyWriteSharedMemory events with length equal to _R_.[[ElementSize]].
        1. Return ComposeWriteEventBytes(_execution_, _R_.[[ByteIndex]], _Ws_).