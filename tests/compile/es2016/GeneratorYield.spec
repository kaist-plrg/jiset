          1. Assert: _iterNextObj_ is an Object that implements the <i>IteratorResult</i> interface.
          1. Let _genContext_ be the running execution context.
          1. Assert: _genContext_ is the execution context of a generator.
          1. Let _generator_ be the value of the Generator component of _genContext_.
          1. Set the value of _generator_'s [[GeneratorState]] internal slot to `"suspendedYield"`.
          1. Remove _genContext_ from the execution context stack and restore the execution context that is at the top of the execution context stack as the running execution context.
          1. Set the code evaluation state of _genContext_ such that when evaluation is resumed with a Completion _resumptionValue_ the following steps will be performed:
            1. Return _resumptionValue_.
            1. NOTE: This returns to the evaluation of the |YieldExpression| production that originally called this abstract operation.
          1. Return NormalCompletion(_iterNextObj_).
          1. NOTE: This returns to the evaluation of the operation that had most previously resumed evaluation of _genContext_.