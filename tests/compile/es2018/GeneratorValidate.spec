          1. If Type(_generator_) is not Object, throw a *TypeError* exception.
          1. If _generator_ does not have a [[GeneratorState]] internal slot, throw a *TypeError* exception.
          1. Assert: _generator_ also has a [[GeneratorContext]] internal slot.
          1. Let _state_ be _generator_.[[GeneratorState]].
          1. If _state_ is `"executing"`, throw a *TypeError* exception.
          1. Return _state_.