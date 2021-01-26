          1. Let _keys_ be a new empty List.
          1. Assert: _O_ is an Integer-Indexed exotic object.
          1. Let _len_ be _O_.[[ArrayLength]].
          1. For each integer _i_ starting with 0 such that _i_ < _len_, in ascending order, do
            1. Add ! ToString(_i_) as the last element of _keys_.
          1. For each own property key _P_ of _O_ such that Type(_P_) is String and _P_ is not an integer index, in ascending chronological order of property creation, do
            1. Add _P_ as the last element of _keys_.
          1. For each own property key _P_ of _O_ such that Type(_P_) is Symbol, in ascending chronological order of property creation, do
            1. Add _P_ as the last element of _keys_.
          1. Return _keys_.