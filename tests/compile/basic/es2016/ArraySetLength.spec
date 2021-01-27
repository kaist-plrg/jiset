          1. If the [[Value]] field of _Desc_ is absent, then
            1. Return OrdinaryDefineOwnProperty(_A_, `"length"`, _Desc_).
          1. Let _newLenDesc_ be a copy of _Desc_.
          1. Let _newLen_ be ? ToUint32(_Desc_.[[Value]]).
          1. Let _numberLen_ be ? ToNumber(_Desc_.[[Value]]).
          1. If _newLen_ ≠ _numberLen_, throw a *RangeError* exception.
          1. Set _newLenDesc_.[[Value]] to _newLen_.
          1. Let _oldLenDesc_ be OrdinaryGetOwnProperty(_A_, `"length"`).
          1. Assert: _oldLenDesc_ will never be *undefined* or an accessor descriptor because Array objects are created with a length data property that cannot be deleted or reconfigured.
          1. Let _oldLen_ be _oldLenDesc_.[[Value]].
          1. If _newLen_ ≥ _oldLen_, then
            1. Return OrdinaryDefineOwnProperty(_A_, `"length"`, _newLenDesc_).
          1. If _oldLenDesc_.[[Writable]] is *false*, return *false*.
          1. If _newLenDesc_.[[Writable]] is absent or has the value *true*, let _newWritable_ be *true*.
          1. Else,
            1. Need to defer setting the [[Writable]] attribute to *false* in case any elements cannot be deleted.
            1. Let _newWritable_ be *false*.
            1. Set _newLenDesc_.[[Writable]] to *true*.
          1. Let _succeeded_ be ! OrdinaryDefineOwnProperty(_A_, `"length"`, _newLenDesc_).
          1. If _succeeded_ is *false*, return *false*.
          1. While _newLen_ < _oldLen_ repeat,
            1. Set _oldLen_ to _oldLen_ - 1.
            1. Let _deleteSucceeded_ be ! _A_.[[Delete]](! ToString(_oldLen_)).
            1. If _deleteSucceeded_ is *false*, then
              1. Set _newLenDesc_.[[Value]] to _oldLen_ + 1.
              1. If _newWritable_ is *false*, set _newLenDesc_.[[Writable]] to *false*.
              1. Let _succeeded_ be ! OrdinaryDefineOwnProperty(_A_, `"length"`, _newLenDesc_).
              1. Return *false*.
          1. If _newWritable_ is *false*, then
            1. Return OrdinaryDefineOwnProperty(_A_, `"length"`, PropertyDescriptor{[[Writable]]: *false*}). This call will always return *true*.
          1. Return *true*.