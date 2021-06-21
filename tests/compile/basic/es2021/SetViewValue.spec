          1. Perform ? RequireInternalSlot(_view_, [[DataView]]).
          1. Assert: _view_ has a [[ViewedArrayBuffer]] internal slot.
          1. Let _getIndex_ be ? ToIndex(_requestIndex_).
          1. If ! IsBigIntElementType(_type_) is *true*, let _numberValue_ be ? ToBigInt(_value_).
          1. Otherwise, let _numberValue_ be ? ToNumber(_value_).
          1. Set _isLittleEndian_ to ! ToBoolean(_isLittleEndian_).
          1. Let _buffer_ be _view_.[[ViewedArrayBuffer]].
          1. If IsDetachedBuffer(_buffer_) is *true*, throw a *TypeError* exception.
          1. Let _viewOffset_ be _view_.[[ByteOffset]].
          1. Let _viewSize_ be _view_.[[ByteLength]].
          1. Let _elementSize_ be the Element Size value specified in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for Element Type _type_.
          1. If _getIndex_ + _elementSize_ > _viewSize_, throw a *RangeError* exception.
          1. Let _bufferIndex_ be _getIndex_ + _viewOffset_.
          1. Return SetValueInBuffer(_buffer_, _bufferIndex_, _type_, _numberValue_, *false*, ~Unordered~, _isLittleEndian_).