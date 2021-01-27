          1. Assert: The value of _generator_'s [[GeneratorState]] internal slot is *undefined*.
          1. Let _genContext_ be the running execution context.
          1. Set the Generator component of _genContext_ to _generator_.
          1. Set the code evaluation state of _genContext_ such that when evaluation is resumed for that execution context the following steps will be performed:
            1. Let _result_ be the result of evaluating _generatorBody_.
            1. Assert: If we return here, the generator either threw an exception or performed either an implicit or explicit return.
            1. Remove _genContext_ from the execution context stack and restore the execution context that is at the top of the execution context stack as the running execution context.
            1. Set _generator_'s [[GeneratorState]] internal slot to `"completed"`.
            1. Once a generator enters the `"completed"` state it never leaves it and its associated execution context is never resumed. Any execution state associated with _generator_ can be discarded at this point.
            1. If _result_ is a normal completion, let _resultValue_ be *undefined*.
            1. Else,
              1. If _result_.[[Type]] is ~return~, let _resultValue_ be _result_.[[Value]].
              1. Else, return Completion(_result_).
            1. Return CreateIterResultObject(_resultValue_, *true*).
          1. Set _generator_'s [[GeneratorContext]] internal slot to _genContext_.
          1. Set _generator_'s [[GeneratorState]] internal slot to `"suspendedStart"`.
          1. Return NormalCompletion(*undefined*).