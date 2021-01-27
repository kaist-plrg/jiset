              1. Let _O_ be the *this* value.
              1. Assert: Type(_O_) is Object.
              1. Assert: _O_ has all of the internal slots of a For-In Iterator Instance (<emu-xref href="#sec-properties-of-for-in-iterator-instances"></emu-xref>).
              1. Let _object_ be _O_.[[Object]].
              1. Let _visited_ be _O_.[[VisitedKeys]].
              1. Let _remaining_ be _O_.[[RemainingKeys]].
              1. Repeat,
                1. If _O_.[[ObjectWasVisited]] is *false*, then
                  1. Let _keys_ be ? _object_.[[OwnPropertyKeys]]().
                  1. For each element _key_ of _keys_, do
                    1. If Type(_key_) is String, then
                      1. Append _key_ to _remaining_.
                  1. Set _O_.[[ObjectWasVisited]] to *true*.
                1. Repeat, while _remaining_ is not empty,
                  1. Let _r_ be the first element of _remaining_.
                  1. Remove the first element from _remaining_.
                  1. If there does not exist an element _v_ of _visited_ such that SameValue(_r_, _v_) is *true*, then
                    1. Let _desc_ be ? _object_.[[GetOwnProperty]](_r_).
                    1. If _desc_ is not *undefined*, then
                      1. Append _r_ to _visited_.
                      1. If _desc_.[[Enumerable]] is *true*, return CreateIterResultObject(_r_, *false*).
                1. Set _object_ to ? _object_.[[GetPrototypeOf]]().
                1. Set _O_.[[Object]] to _object_.
                1. Set _O_.[[ObjectWasVisited]] to *false*.
                1. If _object_ is *null*, return CreateIterResultObject(*undefined*, *true*).