          1. Assert: Type(_string_) is String.
          1. Assert: Type(_searchValue_) is String.
          1. Assert: _fromIndex_ is a non-negative integer.
          1. Let _len_ be the length of _string_.
          1. If _searchValue_ is the empty String and _fromIndex_ ≤ _len_, return _fromIndex_.
          1. Let _searchLen_ be the length of _searchValue_.
          1. For each integer _i_ starting with _fromIndex_ such that _i_ ≤ _len_ - _searchLen_, in ascending order, do
            1. Let _candidate_ be the substring of _string_ from _i_ to _i_ + _searchLen_.
            1. If _candidate_ is the same sequence of code units as _searchValue_, return _i_.
          1. Return -1.