          1. Assert: _typedArray_ is an Object that has a [[ViewedArrayBuffer]] internal slot.
          1. Let _accessIndex_ be ? ToIndex(_requestIndex_).
          1. Let _length_ be _typedArray_.[[ArrayLength]].
          1. Assert: _accessIndex_ ≥ 0.
          1. If _accessIndex_ ≥ _length_, throw a *RangeError* exception.
          1. Return _accessIndex_.