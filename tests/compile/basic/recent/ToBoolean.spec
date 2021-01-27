* If Type(_argument_) is Undefined,
  * Return *false*.
* If Type(_argument_) is Null,
  * Return *false*.
* If Type(_argument_) is Boolean,
  * Return _argument_.
* If Type(_argument_) is Number,
  * If _argument_ is *+0*𝔽, *-0*𝔽, or *NaN*, return *false*; otherwise return *true*.
* If Type(_argument_) is String,
  * If _argument_ is the empty String (its length is 0), return *false*; otherwise return *true*.
* If Type(_argument_) is Symbol,
  * Return *true*.
* If Type(_argument_) is BigInt,
  * If _argument_ is *0*ℤ, return *false*; otherwise return *true*.
* If Type(_argument_) is Object,
  * Return *true*. An alternate algorithm related to the [[IsHTMLDDA]] internal slot is mandated in section .