          1. Let _C_ be the active function object.
          1. Let _args_ be the _argumentsList_ that was passed to this function by [[Call]] or [[Construct]].
          1. Return ? CreateDynamicFunction(_C_, NewTarget, `"generator"`, _args_).