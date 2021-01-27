          1. If _stack_ contains _value_, throw a *TypeError* exception because the structure is cyclical.
          1. Append _value_ to _stack_.
          1. Let _stepback_ be _indent_.
          1. Set _indent_ to the concatenation of _indent_ and _gap_.
          1. Let _partial_ be a new empty List.
          1. Let _len_ be ? ToLength(? Get(_value_, `"length"`)).
          1. Let _index_ be 0.
          1. Repeat, while _index_ < _len_
            1. Let _strP_ be ? SerializeJSONProperty(! ToString(_index_), _value_).
            1. If _strP_ is *undefined*, then
              1. Append `"null"` to _partial_.
            1. Else,
              1. Append _strP_ to _partial_.
            1. Increment _index_ by 1.
          1. If _partial_ is empty, then
            1. Let _final_ be `"[]"`.
          1. Else,
            1. If _gap_ is the empty String, then
              1. Let _properties_ be a String formed by concatenating all the element Strings of _partial_ with each adjacent pair of Strings separated with code unit 0x002C (COMMA). A comma is not inserted either before the first String or after the last String.
              1. Let _final_ be the result of concatenating `"["`, _properties_, and `"]"`.
            1. Else,
              1. Let _separator_ be the result of concatenating code unit 0x002C (COMMA), code unit 0x000A (LINE FEED), and _indent_.
              1. Let _properties_ be a String formed by concatenating all the element Strings of _partial_ with each adjacent pair of Strings separated with _separator_. The _separator_ String is not inserted either before the first String or after the last String.
              1. Let _final_ be the result of concatenating `"["`, code unit 0x000A (LINE FEED), _indent_, _properties_, code unit 0x000A (LINE FEED), _stepback_, and `"]"`.
          1. Remove the last element of _stack_.
          1. Set _indent_ to _stepback_.
          1. Return _final_.