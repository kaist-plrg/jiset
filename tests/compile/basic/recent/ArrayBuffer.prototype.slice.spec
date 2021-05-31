          1. Let _O_ be the *this* value.
          1. Perform ? RequireInternalSlot(_O_, [[ArrayBufferData]]).
          1. If IsSharedArrayBuffer(_O_) is *true*, throw a *TypeError* exception.
          1. If IsDetachedBuffer(_O_) is *true*, throw a *TypeError* exception.
          1. Let _len_ be _O_.[[ArrayBufferByteLength]].
          1. Let _relativeStart_ be ? ToIntegerOrInfinity(_start_).
          1. If _relativeStart_ is -∞, let _first_ be 0.
          1. Else if _relativeStart_ < 0, let _first_ be max(_len_ + _relativeStart_, 0).
          1. Else, let _first_ be min(_relativeStart_, _len_).
          1. If _end_ is *undefined*, let _relativeEnd_ be _len_; else let _relativeEnd_ be ? ToIntegerOrInfinity(_end_).
          1. If _relativeEnd_ is -∞, let _final_ be 0.
          1. Else if _relativeEnd_ < 0, let _final_ be max(_len_ + _relativeEnd_, 0).
          1. Else, let _final_ be min(_relativeEnd_, _len_).
          1. Let _newLen_ be max(_final_ - _first_, 0).
          1. Let _ctor_ be ? SpeciesConstructor(_O_, %ArrayBuffer%).
          1. Let _new_ be ? Construct(_ctor_, « 𝔽(_newLen_) »).
          1. Perform ? RequireInternalSlot(_new_, [[ArrayBufferData]]).
          1. If IsSharedArrayBuffer(_new_) is *true*, throw a *TypeError* exception.
          1. If IsDetachedBuffer(_new_) is *true*, throw a *TypeError* exception.
          1. If SameValue(_new_, _O_) is *true*, throw a *TypeError* exception.
          1. If _new_.[[ArrayBufferByteLength]] < _newLen_, throw a *TypeError* exception.
          1. NOTE: Side-effects of the above steps may have detached _O_.
          1. If IsDetachedBuffer(_O_) is *true*, throw a *TypeError* exception.
          1. Let _fromBuf_ be _O_.[[ArrayBufferData]].
          1. Let _toBuf_ be _new_.[[ArrayBufferData]].
          1. Perform CopyDataBlockBytes(_toBuf_, 0, _fromBuf_, _first_, _newLen_).
          1. Return _new_.