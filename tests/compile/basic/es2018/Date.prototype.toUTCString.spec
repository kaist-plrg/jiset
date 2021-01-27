          1. Let _O_ be this Date object.
          1. Let _tv_ be ? thisTimeValue(_O_).
          1. If _tv_ is *NaN*, return `"Invalid Date"`.
          1. Let _weekday_ be the Name of the entry in <emu-xref href="#sec-todatestring-day-names"></emu-xref> with the Number WeekDay(_tv_).
          1. Let _month_ be the Name of the entry in <emu-xref href="#sec-todatestring-month-names"></emu-xref> with the Number MonthFromTime(_tv_).
          1. Let _day_ be the String representation of DateFromTime(_tv_), formatted as a two-digit decimal number, padded to the left with a zero if necessary.
          1. Let _year_ be the String representation of YearFromTime(_tv_), formatted as a decimal number of at least four digits, padded to the left with zeroes if necessary.
          1. Return the string-concatenation of _weekday_, `","`, the code unit 0x0020 (SPACE), _day_, the code unit 0x0020 (SPACE), _month_, the code unit 0x0020 (SPACE), _year_, the code unit 0x0020 (SPACE), and TimeString(_tv_).