        1. Let _byteLocation_ be _byteIndex_.
        1. Let _bytesRead_ be a new empty List.
        1. For each element _W_ of _Ws_, do
          1. Assert: _W_ has _byteLocation_ in its range.
          1. Let _payloadIndex_ be _byteLocation_ - _W_.[[ByteIndex]].
          1. If _W_ is a WriteSharedMemory event, then
            1. Let _byte_ be _W_.[[Payload]][_payloadIndex_].
          1. Else,
            1. Assert: _W_ is a ReadModifyWriteSharedMemory event.
            1. Let _bytes_ be ValueOfReadEvent(_execution_, _W_).
            1. Let _bytesModified_ be _W_.[[ModifyOp]](_bytes_, _W_.[[Payload]]).
            1. Let _byte_ be _bytesModified_[_payloadIndex_].
          1. Append _byte_ to _bytesRead_.
          1. Set _byteLocation_ to _byteLocation_ + 1.
        1. Return _bytesRead_.