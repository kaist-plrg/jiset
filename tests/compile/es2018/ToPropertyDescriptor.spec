          1. If Type(_Obj_) is not Object, throw a *TypeError* exception.
          1. Let _desc_ be a new Property Descriptor that initially has no fields.
          1. Let _hasEnumerable_ be ? HasProperty(_Obj_, `"enumerable"`).
          1. If _hasEnumerable_ is *true*, then
            1. Let _enum_ be ToBoolean(? Get(_Obj_, `"enumerable"`)).
            1. Set _desc_.[[Enumerable]] to _enum_.
          1. Let _hasConfigurable_ be ? HasProperty(_Obj_, `"configurable"`).
          1. If _hasConfigurable_ is *true*, then
            1. Let _conf_ be ToBoolean(? Get(_Obj_, `"configurable"`)).
            1. Set _desc_.[[Configurable]] to _conf_.
          1. Let _hasValue_ be ? HasProperty(_Obj_, `"value"`).
          1. If _hasValue_ is *true*, then
            1. Let _value_ be ? Get(_Obj_, `"value"`).
            1. Set _desc_.[[Value]] to _value_.
          1. Let _hasWritable_ be ? HasProperty(_Obj_, `"writable"`).
          1. If _hasWritable_ is *true*, then
            1. Let _writable_ be ToBoolean(? Get(_Obj_, `"writable"`)).
            1. Set _desc_.[[Writable]] to _writable_.
          1. Let _hasGet_ be ? HasProperty(_Obj_, `"get"`).
          1. If _hasGet_ is *true*, then
            1. Let _getter_ be ? Get(_Obj_, `"get"`).
            1. If IsCallable(_getter_) is *false* and _getter_ is not *undefined*, throw a *TypeError* exception.
            1. Set _desc_.[[Get]] to _getter_.
          1. Let _hasSet_ be ? HasProperty(_Obj_, `"set"`).
          1. If _hasSet_ is *true*, then
            1. Let _setter_ be ? Get(_Obj_, `"set"`).
            1. If IsCallable(_setter_) is *false* and _setter_ is not *undefined*, throw a *TypeError* exception.
            1. Set _desc_.[[Set]] to _setter_.
          1. If _desc_.[[Get]] is present or _desc_.[[Set]] is present, then
            1. If _desc_.[[Value]] is present or _desc_.[[Writable]] is present, throw a *TypeError* exception.
          1. Return _desc_.