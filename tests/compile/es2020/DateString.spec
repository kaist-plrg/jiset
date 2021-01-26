            1. Assert: Type(_tv_) is Number.
            1. Assert: _tv_ is not *NaN*.
            1. Let _weekday_ be the Name of the entry in <emu-xref href="#sec-todatestring-day-names"></emu-xref> with the Number WeekDay(_tv_).
            1. Let _month_ be the Name of the entry in <emu-xref href="#sec-todatestring-month-names"></emu-xref> with the Number MonthFromTime(_tv_).
            1. Let _day_ be the String representation of DateFromTime(_tv_), formatted as a two-digit decimal number, padded to the left with a zero if necessary.
            1. Let _yv_ be YearFromTime(_tv_).
            1. If _yv_ ≥ 0, let _yearSign_ be the empty String; otherwise, let _yearSign_ be *"-"*.
            1. Let _year_ be the String representation of abs(_yv_), formatted as a decimal number.
            1. Let _paddedYear_ be ! StringPad(_year_, 4, *"0"*, ~start~).
            1. Return the string-concatenation of _weekday_, the code unit 0x0020 (SPACE), _month_, the code unit 0x0020 (SPACE), _day_, the code unit 0x0020 (SPACE), _yearSign_, and _paddedYear_.