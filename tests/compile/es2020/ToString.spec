* If Type(_argument_) is Undefined,
  * Return *"undefined"*.
* If Type(_argument_) is Null,
  * Return *"null"*.
* If Type(_argument_) is Boolean,
  * If _argument_ is *true*, return *"true"*. If _argument_ is *false*, return *"false"*.
* If Type(_argument_) is Number,
  * Return ! Number::toString(_argument_).
* If Type(_argument_) is String,
  * Return _argument_.
* If Type(_argument_) is Symbol,
  * Throw a *TypeError* exception.
* If Type(_argument_) is BigInt,
  * Return ! BigInt::toString(_argument_).
* If Type(_argument_) is Object,
  1. Let _primValue_ be ? ToPrimitive(_argument_, hint String).
  1. Return ? ToString(_primValue_).