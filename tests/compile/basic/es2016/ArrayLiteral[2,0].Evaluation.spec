          1. Let _array_ be ArrayCreate(0).
          1. Let _len_ be the result of performing ArrayAccumulation for |ElementList| with arguments _array_ and 0.
          1. ReturnIfAbrupt(_len_).
          1. Let _padding_ be the ElisionWidth of |Elision|; if |Elision| is not present, use the numeric value zero.
          1. Perform Set(_array_, `"length"`, ToUint32(_padding_+_len_), *false*).
          1. NOTE: The above Set cannot fail because of the nature of the object returned by ArrayCreate.
          1. Return _array_.