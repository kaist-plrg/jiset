          1. Assert: Type(_array_) is Object.
          1. Let _iterator_ be ObjectCreate(%ArrayIteratorPrototype%, « [[IteratedObject]], [[ArrayIteratorNextIndex]], [[ArrayIterationKind]] »).
          1. Set _iterator_.[[IteratedObject]] to _array_.
          1. Set _iterator_.[[ArrayIteratorNextIndex]] to 0.
          1. Set _iterator_.[[ArrayIterationKind]] to _kind_.
          1. Return _iterator_.