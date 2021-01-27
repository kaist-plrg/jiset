          1. Let _P_ be ? ToPropertyKey(_V_).
          1. Let _O_ be ? ToObject(*this* value).
          1. Let _desc_ be ? _O_.[[GetOwnProperty]](_P_).
          1. If _desc_ is *undefined*, return *false*.
          1. Return _desc_.[[Enumerable]].