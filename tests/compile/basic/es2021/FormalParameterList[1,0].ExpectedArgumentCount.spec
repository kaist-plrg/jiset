        1. Let _count_ be ExpectedArgumentCount of |FormalParameterList|.
        1. If HasInitializer of |FormalParameterList| is *true* or HasInitializer of |FormalParameter| is *true*, return _count_.
        1. Return _count_ + 1.