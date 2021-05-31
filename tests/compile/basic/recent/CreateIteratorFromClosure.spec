          1. NOTE: _closure_ can contain uses of the Yield shorthand to yield an IteratorResult object.
          1. Let _internalSlotsList_ be « [[GeneratorState]], [[GeneratorContext]], [[GeneratorBrand]] ».
          1. Let _generator_ be ! OrdinaryObjectCreate(_generatorPrototype_, _internalSlotsList_).
          1. Set _generator_.[[GeneratorBrand]] to _generatorBrand_.
          1. Set _generator_.[[GeneratorState]] to *undefined*.
          1. Perform ! GeneratorStart(_generator_, _closure_).
          1. Return _generator_.