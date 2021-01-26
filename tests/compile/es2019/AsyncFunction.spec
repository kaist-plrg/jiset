          1. Let _C_ be the active function object.
          2. Let _args_ be the _argumentsList_ that was passed to this function by [[Call]] or [[Construct]].
          3. Return CreateDynamicFunction(_C_, NewTarget, `"async"`, _args_).