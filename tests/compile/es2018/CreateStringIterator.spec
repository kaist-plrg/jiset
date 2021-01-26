          1. Assert: Type(_string_) is String.
          1. Let _iterator_ be ObjectCreate(%StringIteratorPrototype%, « [[IteratedString]], [[StringIteratorNextIndex]] »).
          1. Set _iterator_.[[IteratedString]] to _string_.
          1. Set _iterator_.[[StringIteratorNextIndex]] to 0.
          1. Return _iterator_.