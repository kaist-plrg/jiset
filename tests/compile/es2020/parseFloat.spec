        1. Let _inputString_ be ? ToString(_string_).
        1. Let _trimmedString_ be ! TrimString(_inputString_, ~start~).
        1. If neither _trimmedString_ nor any prefix of _trimmedString_ satisfies the syntax of a |StrDecimalLiteral| (see <emu-xref href="#sec-tonumber-applied-to-the-string-type"></emu-xref>), return *NaN*.
        1. Let _numberString_ be the longest prefix of _trimmedString_, which might be _trimmedString_ itself, that satisfies the syntax of a |StrDecimalLiteral|.
        1. Let _mathFloat_ be MV of _numberString_.
        1. If _mathFloat_ = 0<sub>ℝ</sub>, then
          1. If the first code unit of _trimmedString_ is the code unit 0x002D (HYPHEN-MINUS), return *-0*.
          1. Return *+0*.
        1. Return the Number value for _mathFloat_.