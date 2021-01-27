          1. Let _genContext_ be the running execution context.
          1. If _genContext_ does not have a Generator component, return ~non-generator~.
          1. Let _generator_ be the Generator component of _genContext_.
          1. If _generator_ has an [[AsyncGeneratorState]] internal slot, return ~async~.
          1. Else, return ~sync~.