* If Type(_argument_) is Undefined,
  * Return *false*.
* If Type(_argument_) is Null,
  * Return *false*.
* If Type(_argument_) is Boolean,
  * Return _argument_.
* If Type(_argument_) is Number,
  * Return *false* if _argument_ is *+0*, *-0*, or *NaN*; otherwise return *true*.
* If Type(_argument_) is String,
  * Return *false* if _argument_ is the empty String (its length is zero); otherwise return *true*.
* If Type(_argument_) is Symbol,
  * Return *true*.
* If Type(_argument_) is Object,
  * Return *true*.