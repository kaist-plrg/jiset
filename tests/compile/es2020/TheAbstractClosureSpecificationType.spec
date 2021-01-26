        1. Let _addend_ be 41.
        1. Let _closure_ be a new abstract closure with parameters (_x_) that captures _addend_ and performs the following steps when called:
          1. Return _x_ + _addend_.
        1. Let _val_ be _closure_(1).
        1. Assert: _val_ is 42.