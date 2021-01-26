          1. Let _state_ be ? GeneratorValidate(_generator_).
          1. If _state_ is `"suspendedStart"`, then
            1. Set _generator_'s [[GeneratorState]] internal slot to `"completed"`.
            1. Once a generator enters the `"completed"` state it never leaves it and its associated execution context is never resumed. Any execution state associated with _generator_ can be discarded at this point.
            1. Let _state_ be `"completed"`.
          1. If _state_ is `"completed"`, then
            1. If _abruptCompletion_.[[Type]] is ~return~, then
              1. Return CreateIterResultObject(_abruptCompletion_.[[Value]], *true*).
            1. Return Completion(_abruptCompletion_).
          1. Assert: _state_ is `"suspendedYield"`.
          1. Let _genContext_ be the value of _generator_'s [[GeneratorContext]] internal slot.
          1. Let _methodContext_ be the running execution context.
          1. Suspend _methodContext_.
          1. Set _generator_'s [[GeneratorState]] internal slot to `"executing"`.
          1. Push _genContext_ onto the execution context stack; _genContext_ is now the running execution context.
          1. Resume the suspended evaluation of _genContext_ using _abruptCompletion_ as the result of the operation that suspended it. Let _result_ be the completion record returned by the resumed computation.
          1. Assert: When we return here, _genContext_ has already been removed from the execution context stack and _methodContext_ is the currently running execution context.
          1. Return Completion(_result_).