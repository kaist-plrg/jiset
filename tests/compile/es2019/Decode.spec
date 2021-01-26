            1. Let _strLen_ be the number of code units in _string_.
            1. Let _R_ be the empty String.
            1. Let _k_ be 0.
            1. Repeat,
              1. If _k_ equals _strLen_, return _R_.
              1. Let _C_ be the code unit at index _k_ within _string_.
              1. If _C_ is not the code unit 0x0025 (PERCENT SIGN), then
                1. Let _S_ be the String value containing only the code unit _C_.
              1. Else _C_ is the code unit 0x0025 (PERCENT SIGN),
                1. Let _start_ be _k_.
                1. If _k_ + 2 is greater than or equal to _strLen_, throw a *URIError* exception.
                1. If the code units at index (_k_ + 1) and (_k_ + 2) within _string_ do not represent hexadecimal digits, throw a *URIError* exception.
                1. Let _B_ be the 8-bit value represented by the two hexadecimal digits at index (_k_ + 1) and (_k_ + 2).
                1. Increment _k_ by 2.
                1. If the most significant bit in _B_ is 0, then
                  1. Let _C_ be the code unit whose value is _B_.
                  1. If _C_ is not in _reservedSet_, then
                    1. Let _S_ be the String value containing only the code unit _C_.
                  1. Else _C_ is in _reservedSet_,
                    1. Let _S_ be the substring of _string_ from index _start_ to index _k_ inclusive.
                1. Else the most significant bit in _B_ is 1,
                  1. Let _n_ be the smallest nonnegative integer such that (_B_ << _n_) & 0x80 is equal to 0.
                  1. If _n_ equals 1 or _n_ is greater than 4, throw a *URIError* exception.
                  1. Let _Octets_ be a List of 8-bit integers of size _n_.
                  1. Set _Octets_[0] to _B_.
                  1. If _k_ + (3 × (_n_ - 1)) is greater than or equal to _strLen_, throw a *URIError* exception.
                  1. Let _j_ be 1.
                  1. Repeat, while _j_ < _n_
                    1. Increment _k_ by 1.
                    1. If the code unit at index _k_ within _string_ is not the code unit 0x0025 (PERCENT SIGN), throw a *URIError* exception.
                    1. If the code units at index (_k_ + 1) and (_k_ + 2) within _string_ do not represent hexadecimal digits, throw a *URIError* exception.
                    1. Let _B_ be the 8-bit value represented by the two hexadecimal digits at index (_k_ + 1) and (_k_ + 2).
                    1. If the two most significant bits in _B_ are not 10, throw a *URIError* exception.
                    1. Increment _k_ by 2.
                    1. Set _Octets_[_j_] to _B_.
                    1. Increment _j_ by 1.
                  1. If _Octets_ does not contain a valid UTF-8 encoding of a Unicode code point, throw a *URIError* exception.
                  1. Let _V_ be the value obtained by applying the UTF-8 transformation to _Octets_, that is, from a List of octets into a 21-bit value.
                  1. Let _S_ be the String value whose code units are, in order, the elements in UTF16Encoding(_V_).
              1. Set _R_ to the string-concatenation of the previous value of _R_ and _S_.
              1. Increase _k_ by 1.