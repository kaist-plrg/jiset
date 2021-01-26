          1. Let _numberOfArgs_ be the number of arguments passed to this function call.
          1. Assert: _numberOfArgs_ = 0.
          1. If NewTarget is not *undefined*, then
            1. Let _O_ be ? OrdinaryCreateFromConstructor(NewTarget, `"%DatePrototype%"`, « [[DateValue]] »).
            1. Set _O_.[[DateValue]] to the time value (UTC) identifying the current time.
            1. Return _O_.
          1. Else,
            1. Let _now_ be the Number that is the time value (UTC) identifying the current time.
            1. Return ToDateString(_now_).