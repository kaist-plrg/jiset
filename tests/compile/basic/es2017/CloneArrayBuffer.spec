          1. Assert: Type(_srcBuffer_) is Object and it has an [[ArrayBufferData]] internal slot.
          1. If _cloneConstructor_ is not present, then
            1. Set _cloneConstructor_ to ? SpeciesConstructor(_srcBuffer_, %ArrayBuffer%).
            1. If IsDetachedBuffer(_srcBuffer_) is *true*, throw a *TypeError* exception.
          1. Else, Assert: IsConstructor(_cloneConstructor_) is *true*.
          1. Let _srcBlock_ be _srcBuffer_.[[ArrayBufferData]].
          1. Let _targetBuffer_ be ? AllocateArrayBuffer(_cloneConstructor_, _srcLength_).
          1. If IsDetachedBuffer(_srcBuffer_) is *true*, throw a *TypeError* exception.
          1. Let _targetBlock_ be _targetBuffer_.[[ArrayBufferData]].
          1. Perform CopyDataBlockBytes(_targetBlock_, 0, _srcBlock_, _srcByteOffset_, _srcLength_).
          1. Return _targetBuffer_.