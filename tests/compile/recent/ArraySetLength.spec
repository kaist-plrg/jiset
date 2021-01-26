          1. If _Desc_.[[Value]] is absent, then
            1. Return OrdinaryDefineOwnProperty(_A_, *"length"*, _Desc_).
          1. Let _newLenDesc_ be a copy of _Desc_.
          1. [id="step-arraysetlength-newlen"] Let _newLen_ be ? ToUint32(_Desc_.[[Value]]).
          1. [id="step-arraysetlength-numberlen"] Let _numberLen_ be ? ToNumber(_Desc_.[[Value]]).
          1. If _newLen_ is not the same value as _numberLen_, throw a *RangeError* exception.
          1. Set _newLenDesc_.[[Value]] to _newLen_.
          1. Let _oldLenDesc_ be OrdinaryGetOwnProperty(_A_, *"length"*).
          1. Assert: ! IsDataDescriptor(_oldLenDesc_) is *true*.
          1. Assert: _oldLenDesc_.[[Configurable]] is *false*.
          1. Let _oldLen_ be _oldLenDesc_.[[Value]].
          1. If _newLen_ ≥ _oldLen_, then
            1. Return OrdinaryDefineOwnProperty(_A_, *"length"*, _newLenDesc_).
          1. If _oldLenDesc_.[[Writable]] is *false*, return *false*.
          1. If _newLenDesc_.[[Writable]] is absent or has the value *true*, let _newWritable_ be *true*.
          1. Else,
            1. NOTE: Setting the [[Writable]] attribute to *false* is deferred in case any elements cannot be deleted.
            1. Let _newWritable_ be *false*.
            1. Set _newLenDesc_.[[Writable]] to *true*.
          1. Let _succeeded_ be ! OrdinaryDefineOwnProperty(_A_, *"length"*, _newLenDesc_).
          1. If _succeeded_ is *false*, return *false*.
          1. For each own property key _P_ of _A_ that is an array index, whose numeric value is greater than or equal to _newLen_, in descending numeric index order, do
            1. Let _deleteSucceeded_ be ! _A_.[[Delete]](_P_).
            1. If _deleteSucceeded_ is *false*, then
              1. Set _newLenDesc_.[[Value]] to ! ToUint32(_P_) + *1*<sub>𝔽</sub>.
              1. If _newWritable_ is *false*, set _newLenDesc_.[[Writable]] to *false*.
              1. Perform ! OrdinaryDefineOwnProperty(_A_, *"length"*, _newLenDesc_).
              1. Return *false*.
          1. If _newWritable_ is *false*, then
            1. Let _succeeded_ be ! OrdinaryDefineOwnProperty(_A_, *"length"*, PropertyDescriptor { [[Writable]]: *false* }).
            1. Assert: _succeeded_ is *true*.
          1. Return *true*.