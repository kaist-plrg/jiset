        1. Let _count_ be the ExpectedArgumentCount of |FormalsList|.
        1. If HasInitializer of |FormalsList| is *true* or HasInitializer of |FormalParameter| is *true*, return _count_.
        1. Return _count_+1.