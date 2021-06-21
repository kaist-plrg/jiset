          1. Let _obj_ be ? ToObject(_O_).
          1. Let _ownKeys_ be ? _obj_.[[OwnPropertyKeys]]().
          1. Let _descriptors_ be ! OrdinaryObjectCreate(%Object.prototype%).
          1. For each element _key_ of _ownKeys_, do
            1. Let _desc_ be ? _obj_.[[GetOwnProperty]](_key_).
            1. Let _descriptor_ be ! FromPropertyDescriptor(_desc_).
            1. If _descriptor_ is not *undefined*, perform ! CreateDataPropertyOrThrow(_descriptors_, _key_, _descriptor_).
          1. Return _descriptors_.