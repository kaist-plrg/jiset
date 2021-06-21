          1. Assert: Type(_obj_) is Object and it has an [[ArrayBufferData]] internal slot.
          1. Let _bufferData_ be _obj_.[[ArrayBufferData]].
          1. If _bufferData_ is *null*, return *false*.
          1. If _bufferData_ is a Data Block, return *false*.
          1. Assert: _bufferData_ is a Shared Data Block.
          1. Return *true*.