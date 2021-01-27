          1. Assert: Type(_string_) is String.
          1. Let _iterator_ be OrdinaryObjectCreate(%StringIteratorPrototype%, « [[IteratedString]], [[StringNextIndex]] »).
          1. Set _iterator_.[[IteratedString]] to _string_.
          1. Set _iterator_.[[StringNextIndex]] to 0.
          1. Return _iterator_.