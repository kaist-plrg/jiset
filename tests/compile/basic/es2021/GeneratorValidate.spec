          1. Perform ? RequireInternalSlot(_generator_, [[GeneratorState]]).
          1. Perform ? RequireInternalSlot(_generator_, [[GeneratorBrand]]).
          1. If _generator_.[[GeneratorBrand]] is not the same value as _generatorBrand_, throw a *TypeError* exception.
          1. Assert: _generator_ also has a [[GeneratorContext]] internal slot.
          1. Let _state_ be _generator_.[[GeneratorState]].
          1. If _state_ is ~executing~, throw a *TypeError* exception.
          1. Return _state_.