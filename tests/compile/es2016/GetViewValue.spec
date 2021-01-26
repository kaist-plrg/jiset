          1. If Type(_view_) is not Object, throw a *TypeError* exception.
          1. If _view_ does not have a [[DataView]] internal slot, throw a *TypeError* exception.
          1. Let _numberIndex_ be ? ToNumber(_requestIndex_).
          1. Let _getIndex_ be ToInteger(_numberIndex_).
          1. If _numberIndex_ ≠ _getIndex_ or _getIndex_ < 0, throw a *RangeError* exception.
          1. Let _isLittleEndian_ be ToBoolean(_isLittleEndian_).
          1. Let _buffer_ be the value of _view_'s [[ViewedArrayBuffer]] internal slot.
          1. If IsDetachedBuffer(_buffer_) is *true*, throw a *TypeError* exception.
          1. Let _viewOffset_ be the value of _view_'s [[ByteOffset]] internal slot.
          1. Let _viewSize_ be the value of _view_'s [[ByteLength]] internal slot.
          1. Let _elementSize_ be the Number value of the Element Size value specified in <emu-xref href="#table-49"></emu-xref> for Element Type _type_.
          1. If _getIndex_ + _elementSize_ > _viewSize_, throw a *RangeError* exception.
          1. Let _bufferIndex_ be _getIndex_ + _viewOffset_.
          1. Return GetValueFromBuffer(_buffer_, _bufferIndex_, _type_, _isLittleEndian_).