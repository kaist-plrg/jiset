          1. Perform ? RequireInternalSlot(_generator_, [[AsyncGeneratorContext]]).
          1. Perform ? RequireInternalSlot(_generator_, [[AsyncGeneratorState]]).
          1. Perform ? RequireInternalSlot(_generator_, [[AsyncGeneratorQueue]]).
          1. If _generator_.[[GeneratorBrand]] is not the same value as _generatorBrand_, throw a *TypeError* exception.