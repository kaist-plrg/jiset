          1. If ! IsUnclampedIntegerElementType(_type_) is *true*, return *true*.
          1. If ! IsBigIntElementType(_type_) is *true* and _order_ is not ~Init~ or ~Unordered~, return *true*.
          1. Return *false*.