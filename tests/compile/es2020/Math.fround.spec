          1. If _x_ is *NaN*, return *NaN*.
          1. If _x_ is one of *+0*, *-0*, *+∞*, *-∞*, return _x_.
          1. Let _x32_ be the result of converting _x_ to a value in IEEE 754-2019 binary32 format using roundTiesToEven mode.
          1. Let _x64_ be the result of converting _x32_ to a value in IEEE 754-2019 binary64 format.
          1. Return the ECMAScript Number value corresponding to _x64_.