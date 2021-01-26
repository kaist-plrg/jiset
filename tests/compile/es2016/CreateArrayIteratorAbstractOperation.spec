          1. Assert: Type(_array_) is Object.
          1. Let _iterator_ be ObjectCreate(%ArrayIteratorPrototype%, « [[IteratedObject]], [[ArrayIteratorNextIndex]], [[ArrayIterationKind]] »).
          1. Set _iterator_'s [[IteratedObject]] internal slot to _array_.
          1. Set _iterator_'s [[ArrayIteratorNextIndex]] internal slot to 0.
          1. Set _iterator_'s [[ArrayIterationKind]] internal slot to _kind_.
          1. Return _iterator_.