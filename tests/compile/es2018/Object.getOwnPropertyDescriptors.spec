          1. Let _obj_ be ? ToObject(_O_).
          1. Let _ownKeys_ be ? _obj_.[[OwnPropertyKeys]]().
          1. Let _descriptors_ be ! ObjectCreate(%ObjectPrototype%).
          1. For each element _key_ of _ownKeys_ in List order, do
            1. Let _desc_ be ? _obj_.[[GetOwnProperty]](_key_).
            1. Let _descriptor_ be ! FromPropertyDescriptor(_desc_).
            1. If _descriptor_ is not *undefined*, perform ! CreateDataProperty(_descriptors_, _key_, _descriptor_).
          1. Return _descriptors_.