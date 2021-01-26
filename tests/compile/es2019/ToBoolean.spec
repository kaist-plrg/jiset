* If Type(_argument_) is Undefined,
  * Return *false*.
* If Type(_argument_) is Null,
  * Return *false*.
* If Type(_argument_) is Boolean,
  * Return _argument_.
* If Type(_argument_) is Number,
  * If _argument_ is *+0*, *-0*, or *NaN*, return *false*; otherwise return *true*.
* If Type(_argument_) is String,
  * If _argument_ is the empty String (its length is zero), return *false*; otherwise return *true*.
* If Type(_argument_) is Symbol,
  * Return *true*.
* If Type(_argument_) is Object,
  * Return *true*.