            1. Let _strLen_ be the length of _string_.
            1. Let _R_ be the empty String.
            1. Let _k_ be 0.
            1. Repeat,
              1. If _k_ = _strLen_, return _R_.
              1. Let _C_ be the code unit at index _k_ within _string_.
              1. If _C_ is not the code unit 0x0025 (PERCENT SIGN), then
                1. Let _S_ be the String value containing only the code unit _C_.
              1. Else,
                1. Let _start_ be _k_.
                1. If _k_ + 2 ≥ _strLen_, throw a *URIError* exception.
                1. If the code units at index (_k_ + 1) and (_k_ + 2) within _string_ do not represent hexadecimal digits, throw a *URIError* exception.
                1. Let _B_ be the 8-bit value represented by the two hexadecimal digits at index (_k_ + 1) and (_k_ + 2).
                1. Set _k_ to _k_ + 2.
                1. Let _n_ be the number of leading 1 bits in _B_.
                1. If _n_ = 0, then
                  1. Let _C_ be the code unit whose value is _B_.
                  1. If _C_ is not in _reservedSet_, then
                    1. Let _S_ be the String value containing only the code unit _C_.
                  1. Else,
                    1. Let _S_ be the substring of _string_ from _start_ to _k_ + 1.
                1. Else,
                  1. If _n_ = 1 or _n_ > 4, throw a *URIError* exception.
                  1. If _k_ + (3 × (_n_ - 1)) ≥ _strLen_, throw a *URIError* exception.
                  1. Let _Octets_ be a List whose sole element is _B_.
                  1. Let _j_ be 1.
                  1. Repeat, while _j_ < _n_,
                    1. Set _k_ to _k_ + 1.
                    1. If the code unit at index _k_ within _string_ is not the code unit 0x0025 (PERCENT SIGN), throw a *URIError* exception.
                    1. If the code units at index (_k_ + 1) and (_k_ + 2) within _string_ do not represent hexadecimal digits, throw a *URIError* exception.
                    1. Let _B_ be the 8-bit value represented by the two hexadecimal digits at index (_k_ + 1) and (_k_ + 2).
                    1. Set _k_ to _k_ + 2.
                    1. Append _B_ to _Octets_.
                    1. Set _j_ to _j_ + 1.
                  1. Assert: The length of _Octets_ is _n_.
                  1. If _Octets_ does not contain a valid UTF-8 encoding of a Unicode code point, throw a *URIError* exception.
                  1. Let _V_ be the code point obtained by applying the UTF-8 transformation to _Octets_, that is, from a List of octets into a 21-bit value.
                  1. Let _S_ be UTF16EncodeCodePoint(_V_).
              1. Set _R_ to the string-concatenation of _R_ and _S_.
              1. Set _k_ to _k_ + 1.