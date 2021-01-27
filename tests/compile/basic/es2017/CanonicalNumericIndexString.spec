        1. Assert: Type(_argument_) is String.
        1. If _argument_ is `"-0"`, return *-0*.
        1. Let _n_ be ! ToNumber(_argument_).
        1. If SameValue(! ToString(_n_), _argument_) is *false*, return *undefined*.
        1. Return _n_.