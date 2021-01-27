        1. If _symbol_ is not one of |NewTarget|, |SuperProperty|, |SuperCall|, `super`, or `this`, return *false*.
        1. Let _head_ be CoveredAsyncArrowHead of |CoverCallExpressionAndAsyncArrowHead|.
        1. If _head_ Contains _symbol_ is *true*, return *true*.
        1. Return |AsyncConciseBody| Contains _symbol_.