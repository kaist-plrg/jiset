* If Type(_argument_) is Undefined,
  * Return *NaN*.
* If Type(_argument_) is Null,
  * Return *+0*𝔽.
* If Type(_argument_) is Boolean,
  * If _argument_ is *true*, return *1*𝔽. If _argument_ is *false*, return *+0*𝔽.
* If Type(_argument_) is Number,
  * Return _argument_ (no conversion).
* If Type(_argument_) is String,
  * See grammar and conversion algorithm below.
* If Type(_argument_) is Symbol,
  * Throw a *TypeError* exception.
* If Type(_argument_) is BigInt,
  * Throw a *TypeError* exception.
* If Type(_argument_) is Object,
  1. Let _primValue_ be ? ToPrimitive(_argument_, ~number~).
  1. Return ? ToNumber(_primValue_).