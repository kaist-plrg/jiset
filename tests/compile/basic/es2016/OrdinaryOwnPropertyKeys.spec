          1. Let _keys_ be a new empty List.
          1. For each own property key _P_ of _O_ that is an integer index, in ascending numeric index order
            1. Add _P_ as the last element of _keys_.
          1. For each own property key _P_ of _O_ that is a String but is not an integer index, in ascending chronological order of property creation
            1. Add _P_ as the last element of _keys_.
          1. For each own property key _P_ of _O_ that is a Symbol, in ascending chronological order of property creation
            1. Add _P_ as the last element of _keys_.
          1. Return _keys_.