          1. Assert: Type(_array_) is Object.
          1. Assert: _kind_ is ~key+value~, ~key~, or ~value~.
          1. Let _iterator_ be OrdinaryObjectCreate(%ArrayIteratorPrototype%, « [[IteratedArrayLike]], [[ArrayLikeNextIndex]], [[ArrayLikeIterationKind]] »).
          1. Set _iterator_.[[IteratedArrayLike]] to _array_.
          1. Set _iterator_.[[ArrayLikeNextIndex]] to 0.
          1. Set _iterator_.[[ArrayLikeIterationKind]] to _kind_.
          1. Return _iterator_.