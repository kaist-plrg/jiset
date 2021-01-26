          1. If NewTarget is *undefined*, throw a *TypeError* exception.
          1. Let _byteLength_ be ? ToIndex(_length_).
          1. Return ? AllocateSharedArrayBuffer(NewTarget, _byteLength_).