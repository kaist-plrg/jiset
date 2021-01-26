        1. If _symbol_ is not one of |NewTarget|, |SuperProperty|, |SuperCall|, `super`, or `this`, return *false*.
        2. Let _head_ be CoveredAsyncArrowHead of |CoverCallExpressionAndAsyncArrowHead|.
        3. If _head_ Contains _symbol_ is *true*, return *true*.
        4. Return |AsyncConciseBody| Contains _symbol_.