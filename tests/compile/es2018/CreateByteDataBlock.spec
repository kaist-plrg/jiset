          1. Assert: _size_≥0.
          1. Let _db_ be a new Data Block value consisting of _size_ bytes. If it is impossible to create such a Data Block, throw a *RangeError* exception.
          1. Set all of the bytes of _db_ to 0.
          1. Return _db_.