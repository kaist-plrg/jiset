          1. If _state_.[[Stack]] contains _value_, throw a *TypeError* exception because the structure is cyclical.
          1. Append _value_ to _state_.[[Stack]].
          1. Let _stepback_ be _state_.[[Indent]].
          1. Set _state_.[[Indent]] to the string-concatenation of _state_.[[Indent]] and _state_.[[Gap]].
          1. If _state_.[[PropertyList]] is not *undefined*, then
            1. Let _K_ be _state_.[[PropertyList]].
          1. Else,
            1. Let _K_ be ? EnumerableOwnPropertyNames(_value_, ~key~).
          1. Let _partial_ be a new empty List.
          1. For each element _P_ of _K_, do
            1. Let _strP_ be ? SerializeJSONProperty(_state_, _P_, _value_).
            1. If _strP_ is not *undefined*, then
              1. Let _member_ be QuoteJSONString(_P_).
              1. Set _member_ to the string-concatenation of _member_ and *":"*.
              1. If _state_.[[Gap]] is not the empty String, then
                1. Set _member_ to the string-concatenation of _member_ and the code unit 0x0020 (SPACE).
              1. Set _member_ to the string-concatenation of _member_ and _strP_.
              1. Append _member_ to _partial_.
          1. If _partial_ is empty, then
            1. Let _final_ be *"{}"*.
          1. Else,
            1. If _state_.[[Gap]] is the empty String, then
              1. Let _properties_ be the String value formed by concatenating all the element Strings of _partial_ with each adjacent pair of Strings separated with the code unit 0x002C (COMMA). A comma is not inserted either before the first String or after the last String.
              1. Let _final_ be the string-concatenation of *"{"*, _properties_, and *"}"*.
            1. Else,
              1. Let _separator_ be the string-concatenation of the code unit 0x002C (COMMA), the code unit 0x000A (LINE FEED), and _state_.[[Indent]].
              1. Let _properties_ be the String value formed by concatenating all the element Strings of _partial_ with each adjacent pair of Strings separated with _separator_. The _separator_ String is not inserted either before the first String or after the last String.
              1. Let _final_ be the string-concatenation of *"{"*, the code unit 0x000A (LINE FEED), _state_.[[Indent]], _properties_, the code unit 0x000A (LINE FEED), _stepback_, and *"}"*.
          1. Remove the last element of _state_.[[Stack]].
          1. Set _state_.[[Indent]] to _stepback_.
          1. Return _final_.