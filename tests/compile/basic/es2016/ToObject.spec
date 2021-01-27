* If Type(_argument_) is Undefined,
  * Throw a *TypeError* exception.
* If Type(_argument_) is Null,
  * Throw a *TypeError* exception.
* If Type(_argument_) is Boolean,
  * Return a new Boolean object whose [[BooleanData]] internal slot is set to the value of _argument_. See for a description of Boolean objects.
* If Type(_argument_) is Number,
  * Return a new Number object whose [[NumberData]] internal slot is set to the value of _argument_. See for a description of Number objects.
* If Type(_argument_) is String,
  * Return a new String object whose [[StringData]] internal slot is set to the value of _argument_. See for a description of String objects.
* If Type(_argument_) is Symbol,
  * Return a new Symbol object whose [[SymbolData]] internal slot is set to the value of _argument_. See for a description of Symbol objects.
* If Type(_argument_) is Object,
  * Return _argument_.