          1. Let _genContext_ be the running execution context.
          1. Assert: _genContext_ is the execution context of a generator.
          1. Let _generator_ be the value of the Generator component of _genContext_.
          1. Assert: GetGeneratorKind() is ~async~.
          1. Set _value_ to ? Await(_value_).
          1. Set _generator_.[[AsyncGeneratorState]] to `"suspendedYield"`.
          1. Remove _genContext_ from the execution context stack and restore the execution context that is at the top of the execution context stack as the running execution context.
          1. Set the code evaluation state of _genContext_ such that when evaluation is resumed with a Completion _resumptionValue_ the following steps will be performed:
            1. If _resumptionValue_.[[Type]] is not ~return~, return Completion(_resumptionValue_).
            1. Let _awaited_ be Await(_resumptionValue_.[[Value]]).
            1. If _awaited_.[[Type]] is ~throw~, return Completion(_awaited_).
            1. Assert: _awaited_.[[Type]] is ~normal~.
            1. Return Completion { [[Type]]: ~return~, [[Value]]: _awaited_.[[Value]], [[Target]]: ~empty~ }.
            1. NOTE: When one of the above steps returns, it returns to the evaluation of the |YieldExpression| production that originally called this abstract operation.
          1. Return ! AsyncGeneratorResolve(_generator_, _value_, *false*).
          1. NOTE: This returns to the evaluation of the operation that had most previously resumed evaluation of _genContext_.