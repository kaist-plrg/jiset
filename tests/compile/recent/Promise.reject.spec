          1. Let _C_ be the *this* value.
          1. Let _promiseCapability_ be ? NewPromiseCapability(_C_).
          1. Perform ? Call(_promiseCapability_.[[Reject]], *undefined*, « _r_ »).
          1. Return _promiseCapability_.[[Promise]].