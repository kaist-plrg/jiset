* If Type(_argument_) is Undefined,
  * Throw a *TypeError* exception.
* If Type(_argument_) is Null,
  * Throw a *TypeError* exception.
* If Type(_argument_) is Boolean,
  * Return `1n` if _prim_ is *true* and `0n` if _prim_ is *false*.
* If Type(_argument_) is BigInt,
  * Return _prim_.
* If Type(_argument_) is Number,
  * Throw a *TypeError* exception.
* If Type(_argument_) is String,
  1. Let _n_ be ! StringToBigInt(_prim_).
  1. If _n_ is *NaN*, throw a *SyntaxError* exception.
  1. Return _n_.
* If Type(_argument_) is Symbol,
  * Throw a *TypeError* exception.