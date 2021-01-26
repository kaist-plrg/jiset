            1. Assert: _source_ is any ECMAScript language value other than an Object with a [[TypedArrayName]] internal slot.
            1. Let _targetBuffer_ be _target_.[[ViewedArrayBuffer]].
            1. If IsDetachedBuffer(_targetBuffer_) is *true*, throw a *TypeError* exception.
            1. Let _targetLength_ be _target_.[[ArrayLength]].
            1. Let _targetName_ be the String value of _target_.[[TypedArrayName]].
            1. Let _targetElementSize_ be the Element Size value specified in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _targetName_.
            1. Let _targetType_ be the Element Type value in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _targetName_.
            1. Let _targetByteOffset_ be _target_.[[ByteOffset]].
            1. Let _src_ be ? ToObject(_source_).
            1. Let _srcLength_ be ? LengthOfArrayLike(_src_).
            1. If _targetOffset_ is +∞, throw a *RangeError* exception.
            1. If _srcLength_ + _targetOffset_ > _targetLength_, throw a *RangeError* exception.
            1. Let _targetByteIndex_ be _targetOffset_ × _targetElementSize_ + _targetByteOffset_.
            1. Let _k_ be 0.
            1. Let _limit_ be _targetByteIndex_ + _targetElementSize_ × _srcLength_.
            1. Repeat, while _targetByteIndex_ < _limit_,
              1. Let _Pk_ be ! ToString(𝔽(_k_)).
              1. Let _value_ be ? Get(_src_, _Pk_).
              1. If _target_.[[ContentType]] is ~BigInt~, set _value_ to ? ToBigInt(_value_).
              1. Otherwise, set _value_ to ? ToNumber(_value_).
              1. If IsDetachedBuffer(_targetBuffer_) is *true*, throw a *TypeError* exception.
              1. Perform SetValueInBuffer(_targetBuffer_, _targetByteIndex_, _targetType_, _value_, *true*, ~Unordered~).
              1. Set _k_ to _k_ + 1.
              1. Set _targetByteIndex_ to _targetByteIndex_ + _targetElementSize_.