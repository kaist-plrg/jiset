            1. Let _strLen_ be the number of code units in _string_.
            1. Let _R_ be the empty String.
            1. Let _k_ be 0.
            1. Repeat,
              1. If _k_ equals _strLen_, return _R_.
              1. Let _C_ be the code unit at index _k_ within _string_.
              1. If _C_ is in _unescapedSet_, then
                1. Set _k_ to _k_ + 1.
                1. Set _R_ to the string-concatenation of the previous value of _R_ and _C_.
              1. Else,
                1. Let _cp_ be ! CodePointAt(_string_, _k_).
                1. If _cp_.[[IsUnpairedSurrogate]] is *true*, throw a *URIError* exception.
                1. Set _k_ to _k_ + _cp_.[[CodeUnitCount]].
                1. Let _Octets_ be the List of octets resulting by applying the UTF-8 transformation to _cp_.[[CodePoint]].
                1. For each element _octet_ of _Octets_ in List order, do
                  1. Set _R_ to the string-concatenation of:
                    * the previous value of _R_
                    * *"%"*
                    * the String representation of _octet_, formatted as a two-digit uppercase hexadecimal number, padded to the left with a zero if necessary