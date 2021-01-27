          1. Assert: Type(_srcBuffer_) is Object and it has an [[ArrayBufferData]] internal slot.
          1. If _cloneConstructor_ is not present, then
            1. Let _cloneConstructor_ be ? SpeciesConstructor(_srcBuffer_, %ArrayBuffer%).
            1. If IsDetachedBuffer(_srcBuffer_) is *true*, throw a *TypeError* exception.
          1. Else, Assert: IsConstructor(_cloneConstructor_) is *true*.
          1. Let _srcLength_ be the value of _srcBuffer_'s [[ArrayBufferByteLength]] internal slot.
          1. Assert: _srcByteOffset_ ≤ _srcLength_.
          1. Let _cloneLength_ be _srcLength_ - _srcByteOffset_.
          1. Let _srcBlock_ be the value of _srcBuffer_'s [[ArrayBufferData]] internal slot.
          1. Let _targetBuffer_ be ? AllocateArrayBuffer(_cloneConstructor_, _cloneLength_).
          1. If IsDetachedBuffer(_srcBuffer_) is *true*, throw a *TypeError* exception.
          1. Let _targetBlock_ be the value of _targetBuffer_'s [[ArrayBufferData]] internal slot.
          1. Perform CopyDataBlockBytes(_targetBlock_, 0, _srcBlock_, _srcByteOffset_, _cloneLength_).
          1. Return _targetBuffer_.