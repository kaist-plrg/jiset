        1. Assert: _elements_ is a List whose elements are all ECMAScript language values.
        1. Let _array_ be ! ArrayCreate(0).
        1. Let _n_ be 0.
        1. For each element _e_ of _elements_, do
          1. Perform ! CreateDataPropertyOrThrow(_array_, ! ToString(𝔽(_n_)), _e_).
          1. Set _n_ to _n_ + 1.
        1. Return _array_.