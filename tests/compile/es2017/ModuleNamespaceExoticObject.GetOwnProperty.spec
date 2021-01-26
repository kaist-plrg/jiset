          1. If Type(_P_) is Symbol, return OrdinaryGetOwnProperty(_O_, _P_).
          1. Let _exports_ be _O_.[[Exports]].
          1. If _P_ is not an element of _exports_, return *undefined*.
          1. Let _value_ be ? _O_.[[Get]](_P_, _O_).
          1. Return PropertyDescriptor{[[Value]]: _value_, [[Writable]]: *true*, [[Enumerable]]: *true*, [[Configurable]]: *false* }.