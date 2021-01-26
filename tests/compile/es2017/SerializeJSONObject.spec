          1. If _stack_ contains _value_, throw a *TypeError* exception because the structure is cyclical.
          1. Append _value_ to _stack_.
          1. Let _stepback_ be _indent_.
          1. Set _indent_ to the concatenation of _indent_ and _gap_.
          1. If _PropertyList_ is not *undefined*, then
            1. Let _K_ be _PropertyList_.
          1. Else,
            1. Let _K_ be ? EnumerableOwnProperties(_value_, *"key"*).
          1. Let _partial_ be a new empty List.
          1. For each element _P_ of _K_, do
            1. Let _strP_ be ? SerializeJSONProperty(_P_, _value_).
            1. If _strP_ is not *undefined*, then
              1. Let _member_ be QuoteJSONString(_P_).
              1. Set _member_ to the concatenation of _member_ and the String `":"`.
              1. If _gap_ is not the empty String, then
                1. Set _member_ to the concatenation of _member_ and code unit 0x0020 (SPACE).
              1. Set _member_ to the concatenation of _member_ and _strP_.
              1. Append _member_ to _partial_.
          1. If _partial_ is empty, then
            1. Let _final_ be `"{}"`.
          1. Else,
            1. If _gap_ is the empty String, then
              1. Let _properties_ be a String formed by concatenating all the element Strings of _partial_ with each adjacent pair of Strings separated with code unit 0x002C (COMMA). A comma is not inserted either before the first String or after the last String.
              1. Let _final_ be the result of concatenating `"{"`, _properties_, and `"}"`.
            1. Else _gap_ is not the empty String,
              1. Let _separator_ be the result of concatenating code unit 0x002C (COMMA), code unit 0x000A (LINE FEED), and _indent_.
              1. Let _properties_ be a String formed by concatenating all the element Strings of _partial_ with each adjacent pair of Strings separated with _separator_. The _separator_ String is not inserted either before the first String or after the last String.
              1. Let _final_ be the result of concatenating `"{"`, code unit 0x000A (LINE FEED), _indent_, _properties_, code unit 0x000A (LINE FEED), _stepback_, and `"}"`.
          1. Remove the last element of _stack_.
          1. Set _indent_ to _stepback_.
          1. Return _final_.