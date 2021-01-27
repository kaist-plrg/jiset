            1. If _value_ is an abrupt completion, then
              1. Perform ? Call(_capability_.[[Reject]], *undefined*, « _value_.[[Value]] »).
              1. Return _capability_.[[Promise]].
            1. Else if _value_ is a Completion Record, let _value_ be _value_.[[Value]].