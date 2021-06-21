            1. Assert: _source_ is an Object that has a [[TypedArrayName]] internal slot.
            1. Let _targetBuffer_ be _target_.[[ViewedArrayBuffer]].
            1. If IsDetachedBuffer(_targetBuffer_) is *true*, throw a *TypeError* exception.
            1. Let _targetLength_ be _target_.[[ArrayLength]].
            1. Let _srcBuffer_ be _source_.[[ViewedArrayBuffer]].
            1. If IsDetachedBuffer(_srcBuffer_) is *true*, throw a *TypeError* exception.
            1. Let _targetName_ be the String value of _target_.[[TypedArrayName]].
            1. Let _targetType_ be the Element Type value in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _targetName_.
            1. Let _targetElementSize_ be the Element Size value specified in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _targetName_.
            1. Let _targetByteOffset_ be _target_.[[ByteOffset]].
            1. Let _srcName_ be the String value of _source_.[[TypedArrayName]].
            1. Let _srcType_ be the Element Type value in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _srcName_.
            1. Let _srcElementSize_ be the Element Size value specified in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _srcName_.
            1. Let _srcLength_ be _source_.[[ArrayLength]].
            1. Let _srcByteOffset_ be _source_.[[ByteOffset]].
            1. If _targetOffset_ is +∞, throw a *RangeError* exception.
            1. If _srcLength_ + _targetOffset_ > _targetLength_, throw a *RangeError* exception.
            1. If _target_.[[ContentType]] ≠ _source_.[[ContentType]], throw a *TypeError* exception.
            1. If both IsSharedArrayBuffer(_srcBuffer_) and IsSharedArrayBuffer(_targetBuffer_) are *true*, then
              1. If _srcBuffer_.[[ArrayBufferData]] and _targetBuffer_.[[ArrayBufferData]] are the same Shared Data Block values, let _same_ be *true*; else let _same_ be *false*.
            1. Else, let _same_ be SameValue(_srcBuffer_, _targetBuffer_).
            1. If _same_ is *true*, then
              1. Let _srcByteLength_ be _source_.[[ByteLength]].
              1. Set _srcBuffer_ to ? CloneArrayBuffer(_srcBuffer_, _srcByteOffset_, _srcByteLength_, %ArrayBuffer%).
              1. NOTE: %ArrayBuffer% is used to clone _srcBuffer_ because is it known to not have any observable side-effects.
              1. Let _srcByteIndex_ be 0.
            1. Else, let _srcByteIndex_ be _srcByteOffset_.
            1. Let _targetByteIndex_ be _targetOffset_ × _targetElementSize_ + _targetByteOffset_.
            1. Let _limit_ be _targetByteIndex_ + _targetElementSize_ × _srcLength_.
            1. If _srcType_ is the same as _targetType_, then
              1. NOTE: If _srcType_ and _targetType_ are the same, the transfer must be performed in a manner that preserves the bit-level encoding of the source data.
              1. Repeat, while _targetByteIndex_ < _limit_,
                1. Let _value_ be GetValueFromBuffer(_srcBuffer_, _srcByteIndex_, ~Uint8~, *true*, ~Unordered~).
                1. Perform SetValueInBuffer(_targetBuffer_, _targetByteIndex_, ~Uint8~, _value_, *true*, ~Unordered~).
                1. Set _srcByteIndex_ to _srcByteIndex_ + 1.
                1. Set _targetByteIndex_ to _targetByteIndex_ + 1.
            1. Else,
              1. Repeat, while _targetByteIndex_ < _limit_,
                1. Let _value_ be GetValueFromBuffer(_srcBuffer_, _srcByteIndex_, _srcType_, *true*, ~Unordered~).
                1. Perform SetValueInBuffer(_targetBuffer_, _targetByteIndex_, _targetType_, _value_, *true*, ~Unordered~).
                1. Set _srcByteIndex_ to _srcByteIndex_ + _srcElementSize_.
                1. Set _targetByteIndex_ to _targetByteIndex_ + _targetElementSize_.