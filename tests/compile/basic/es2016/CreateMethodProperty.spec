        1. Assert: Type(_O_) is Object.
        1. Assert: IsPropertyKey(_P_) is *true*.
        1. Let _newDesc_ be the PropertyDescriptor{[[Value]]: _V_, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *true*}.
        1. Return ? _O_.[[DefineOwnProperty]](_P_, _newDesc_).