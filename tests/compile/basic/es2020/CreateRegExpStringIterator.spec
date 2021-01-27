            1. Assert: Type(_S_) is String.
            1. Assert: Type(_global_) is Boolean.
            1. Assert: Type(_fullUnicode_) is Boolean.
            1. Let _iterator_ be OrdinaryObjectCreate(%RegExpStringIteratorPrototype%, « [[IteratingRegExp]], [[IteratedString]], [[Global]], [[Unicode]], [[Done]] »).
            1. Set _iterator_.[[IteratingRegExp]] to _R_.
            1. Set _iterator_.[[IteratedString]] to _S_.
            1. Set _iterator_.[[Global]] to _global_.
            1. Set _iterator_.[[Unicode]] to _fullUnicode_.
            1. Set _iterator_.[[Done]] to *false*.
            1. Return _iterator_.